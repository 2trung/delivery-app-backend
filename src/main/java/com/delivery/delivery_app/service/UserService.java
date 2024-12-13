package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.dto.user.UpdateProfileRequest;
import com.delivery.delivery_app.dto.user.UserDataResponse;
import com.delivery.delivery_app.entity.Driver;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.exception.AppException;
import com.delivery.delivery_app.exception.ErrorCode;
import com.delivery.delivery_app.mapper.UserMapper;
import com.delivery.delivery_app.repository.DriverRepository;
import com.delivery.delivery_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    DriverRepository driverRepository;

    public UserDataResponse getUserInfo() {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
        return userMapper.toUserDataResponse(user);
    }

    public UserDataResponse updateProfile(UpdateProfileRequest request, MultipartFile avatarFile) {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                byte[] avatarBytes = avatarFile.getBytes();
                String base64Avatar = Base64.getEncoder().encodeToString(avatarBytes);
                user.setAvatar(base64Avatar);

            } catch (Exception e) {
                throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toUserDataResponse(updatedUser);
    }

//    public DriverResponse getDriverInfo() {
//        var context = SecurityContextHolder.getContext();
//        String phoneNumber = context.getAuthentication().getName();
//        Driver driver = driverRepository.findByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
//        return userMapper.toDriverResponse(driver);
//    }
}
