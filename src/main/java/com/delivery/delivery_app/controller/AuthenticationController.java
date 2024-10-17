package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.auth.*;
import com.delivery.delivery_app.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/phone-number")
    ApiResponse<InputPhoneNumberResponse> inputPhoneNumber(@RequestBody @Valid InputPhoneNumberRequest request) {
        var result = authenticationService.inputPhoneNumber(request);
        return ApiResponse.<InputPhoneNumberResponse>builder().data(result).build();
    }

    @PostMapping("/register")
    ApiResponse<UserDataResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return ApiResponse.<UserDataResponse>builder().data(authenticationService.register(request)).build();
    }

    @PostMapping("/login")
    ApiResponse<TokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ApiResponse.<TokenResponse>builder().data(authenticationService.login(request)).build();
    }

    @PostMapping("/refresh-token")
    ApiResponse<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ApiResponse.<TokenResponse>builder().data(authenticationService.refreshToken(request)).build();
    }

    @PostMapping("/verify-otp")
    ApiResponse<String> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        return ApiResponse.<String>builder().data(authenticationService.verifyOtp(request)).build();
    }

    @PostMapping("/resend-otp")
    ApiResponse<String> resendOtp(@RequestBody @Valid InputPhoneNumberRequest request) {
        return ApiResponse.<String>builder().data(authenticationService.sendOtp(request)).build();
    }

    @PostMapping("/reset-password")
    ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return ApiResponse.<String>builder().data(authenticationService.resetPassword(request)).build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<String> forgotPassword(@RequestBody @Valid InputPhoneNumberRequest request) {
        return ApiResponse.<String>builder().data(authenticationService.forgotPassword(request)).build();
    }
}
