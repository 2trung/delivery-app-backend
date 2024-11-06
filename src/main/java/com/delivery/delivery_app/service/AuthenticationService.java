package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.PredefinedRole;
import com.delivery.delivery_app.dto.auth.*;
import com.delivery.delivery_app.entity.Otp;
import com.delivery.delivery_app.entity.Role;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.exception.AppException;
import com.delivery.delivery_app.exception.ErrorCode;
import com.delivery.delivery_app.mapper.UserMapper;
import com.delivery.delivery_app.repository.OtpRepository;
import com.delivery.delivery_app.repository.RoleRepository;
import com.delivery.delivery_app.repository.UserRepository;

import com.delivery.delivery_app.utils.TokenType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    OtpRepository otpRepository;
    UserRepository userRepository;
    JwtService jwtService;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    AuthenticationManager authenticationManager;
    public InputPhoneNumberResponse inputPhoneNumber(InputPhoneNumberRequest request) {
        var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
        var user = userRepository.findByPhoneNumber(formattedPhoneNumber);
        if (user.isEmpty()) {
            sendOtp(request);
//            twilioService.sendSMS(formattedPhoneNumber, generatedOtp);
            return InputPhoneNumberResponse.builder().nextAction("register").isExistingUser(false).build();
        } else {
            return InputPhoneNumberResponse.builder().nextAction("login").isExistingUser(true).build();
        }
    }

    public UserDataResponse register(UserRegisterRequest request) {
        var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
        User user = userMapper.toUser(request);
        user.setPhoneNumber(formattedPhoneNumber);

        if (userRepository.findByPhoneNumber(formattedPhoneNumber).isPresent())
            throw new AppException(ErrorCode.USER_EXISTED);

        if (request.getEmail() != null && !request.getEmail().isEmpty() && userRepository.findByEmail(request.getEmail()).isPresent())
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        var otp = otpRepository.findByPhoneNumber(formattedPhoneNumber);
        // validate otp
        if (otp.isEmpty() || otp.get().getExpiredAt().isBefore(Instant.now()) || !passwordEncoder.matches(request.getOtp(), otp.get().getOtp()))
            throw new AppException(ErrorCode.INVALID_OTP);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserDataResponse(user);
    }

    public TokenResponse login(UserLoginRequest request) {
        try {
            var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(formattedPhoneNumber, request.getPassword()));
            var user = userRepository.findByPhoneNumber(formattedPhoneNumber).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
            Set<Role> roles = user.getRoles();
            if (roles.stream().anyMatch(role -> role.getName().equals(PredefinedRole.USER_ROLE))) {
                TokenResponse response = new TokenResponse();
                response.setAccessToken(jwtService.generateToken(user));
                response.setRefreshToken(jwtService.generateRefreshToken(user));
                return response;
            } else {
                throw new AppException(ErrorCode.INVALID_CREDENTIAL);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIAL);
        }
    }

    public TokenResponse driverLogin(UserLoginRequest request) {
        try {
            var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(formattedPhoneNumber, request.getPassword()));
            var user = userRepository.findByPhoneNumber(formattedPhoneNumber).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
            Set<Role> roles = user.getRoles();
            if (roles.stream().anyMatch(role -> role.getName().equals(PredefinedRole.DRIVER_ROLE))) {
                TokenResponse response = new TokenResponse();
                response.setAccessToken(jwtService.generateToken(user));
                response.setRefreshToken(jwtService.generateRefreshToken(user));
                return response;
            } else {
                throw new AppException(ErrorCode.INVALID_CREDENTIAL);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIAL);
        }
    }

    public String forgotPassword(InputPhoneNumberRequest request) {
        var user = userRepository.findByPhoneNumber(formatPhoneNumber(request.getPhoneNumber())).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return sendOtp(request, OtpAction.FORGOT_PASSWORD);
    }

    public String sendOtp(InputPhoneNumberRequest request) {
        return sendOtp(request, OtpAction.REGISTER);
    }

    public String sendOtp(InputPhoneNumberRequest request, OtpAction action) {
        var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
        if (action.equals(OtpAction.FORGOT_PASSWORD))
            userRepository.findByPhoneNumber(formattedPhoneNumber).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var otp = otpRepository.findByPhoneNumber(formattedPhoneNumber);
        if (otp.isEmpty()) {
            createNewOtp(formattedPhoneNumber);
        } else if (otp.get().getResendAt().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
        } else if (otp.get().getRetryCount() <= 6) {
            var generatedOtp = generateOtp(formattedPhoneNumber);
            log.info("OTP: {}", generatedOtp);
            otp.get().setOtp(passwordEncoder.encode(generatedOtp));
            otp.get().setRetryCount(otp.get().getRetryCount() + 1);
            otp.get().setIssuedAt(Instant.now());
            otp.get().setResendAt(Instant.now().plus(1, ChronoUnit.MINUTES));
            otp.get().setExpiredAt(Instant.now().plus(15, ChronoUnit.MINUTES));
            otpRepository.save(otp.get());
        } else if (otp.get().getIssuedAt().isAfter(Instant.now().minus(1, ChronoUnit.DAYS))) {
            throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
        } else {
            otpRepository.delete(otp.get());
            createNewOtp(formattedPhoneNumber);
        }
        return "OTP đã được gửi";

    }

    public String resetPassword(ResetPasswordRequest request) {
        var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
        var otp = otpRepository.findByPhoneNumber(formattedPhoneNumber);
        if (otp.isEmpty() || otp.get().getExpiredAt().isBefore(Instant.now()) || !passwordEncoder.matches(request.getOtp(), otp.get().getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        var user = userRepository.findByPhoneNumber(formattedPhoneNumber).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Đổi mật khẩu thành công";
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        var user = userRepository.findByPhoneNumber(jwtService.extractUsername(request.getRefreshToken(), TokenType.REFRESH_TOKEN)).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        return TokenResponse.builder().accessToken(jwtService.generateToken(user)).refreshToken(request.getRefreshToken()).build();
    }

    public String verifyOtp(VerifyOtpRequest request) {
        var formattedPhoneNumber = formatPhoneNumber(request.getPhoneNumber());
        var otp = otpRepository.findByPhoneNumber(formattedPhoneNumber);
        if (otp.isEmpty() || otp.get().getExpiredAt().isBefore(Instant.now()) || !passwordEncoder.matches(request.getOtp(), otp.get().getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        return "Xác thực thành công";
    }

    public void createNewOtp(String phoneNumber) {
        var generatedOtp = generateOtp(phoneNumber);
        log.info("OTP: {}", generatedOtp);
//        sendSMS(phoneNumber, generatedOtp);
        String otpEncoded = passwordEncoder.encode(generatedOtp);
        Otp otp = Otp.builder().phoneNumber(phoneNumber).otp(otpEncoded).issuedAt(Instant.now()).expiredAt(Instant.now().plus(15, ChronoUnit.MINUTES)).resendAt(Instant.now().plus(1, ChronoUnit.MINUTES)).retryCount(0).build();
        otpRepository.save(otp);
    }

    public void sendSMS(String phoneNumber, String otp) {
        var formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sms.eskooly.com/ajax/send-message.php"))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"devices[]\"\r\n\r\n516\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"mobileNumber\"\r\n\r\n%s\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"prioritize\"\r\n\r\n1\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\nsms\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"attachments[]\"; filename=\"\"\r\nContent-Type: application/octet-stream\r\n\r\n\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv\r\nContent-Disposition: form-data; name=\"message\"\r\n\r\n%s\r\n------WebKitFormBoundary2vUHRePYMdNKCtrv--\r\n", formattedPhoneNumber, otp)))
                .setHeader("accept", "application/json, text/javascript, */*; q=0.01")
                .setHeader("accept-language", "en,en-US;q=0.9,vi;q=0.8")
                .setHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary2vUHRePYMdNKCtrv")
                .setHeader("cookie", "language=English; PHPSESSID=u7achlo8o6gqvvqmqrr6481ki6; SMS_GATEWAY=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3Mjg0OTIwNDEsImp0aSI6InU3YWNobG84bzZncXZ2cW1xcnI2NDgxa2k2IiwiaXNzIjoiIiwibmJmIjoxNzI4NDkyMDQxLCJleHAiOjE3MzM2NzYwNDEsImRhdGEiOiJ1c2VySUR8aToxMzYyO2VtYWlsfHM6MTc6XCJmZXJpdHlzQHlhaG9vLmNvbVwiO25hbWV8czoxMjpcImVTa29vbHkgVXNlclwiO2lzQWRtaW58YjowO3RpbWVab25lfHM6MTI6XCJBc2lhL0thcmFjaGlcIjtMQVNUX0FDVElWSVRZfGk6MTcyODQ5MjA0MTsifQ.uzFnCTLwPYGIEqNGJsdfKERNKmi7BSp0MUBtDUgaizJidR9XE_LYgB3jFybeOXNKARs7KUYJbeoHF71HPJWmIA")
                .setHeader("origin", "https://sms.eskooly.com")
                .setHeader("priority", "u=1, i")
                .setHeader("referer", "https://sms.eskooly.com/sender.php")
                .setHeader("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                .setHeader("sec-ch-ua-mobile", "?0")
                .setHeader("sec-ch-ua-platform", "\"Windows\"")
                .setHeader("sec-fetch-dest", "empty")
                .setHeader("sec-fetch-mode", "cors")
                .setHeader("sec-fetch-site", "same-origin")
                .setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .setHeader("x-requested-with", "XMLHttpRequest")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response: {}", response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Error sending SMS", e);
            Thread.currentThread().interrupt();
        }
    }

    public String generateOtp(String phoneNumber) {
        Random random = new Random();
        int otp = random.nextInt(10000);
        return String.format("%04d", otp);
    }

    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        } else {
            return "+" + phoneNumber;
        }
    }

    public enum OtpAction {
        REGISTER,
        FORGOT_PASSWORD
    }
}
