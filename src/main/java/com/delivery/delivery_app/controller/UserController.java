package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.dto.user.UpdateProfileRequest;
import com.delivery.delivery_app.dto.user.UserDataResponse;
import com.delivery.delivery_app.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping()
    public ApiResponse<UserDataResponse> getUserInfo() {
        return ApiResponse.<UserDataResponse>builder().data(userService.getUserInfo()).build();
    }

    @PutMapping("/update")
    public ApiResponse<UserDataResponse> updateProfile(@RequestParam(value = "avatar", required = false) MultipartFile avatarFile, @ModelAttribute UpdateProfileRequest request) {
        return ApiResponse.<UserDataResponse>builder().data(userService.updateProfile(request, avatarFile)).build();
    }

//    @GetMapping("/driver")
//    public ApiResponse<DriverResponse> getDriverInfo() {
//        return ApiResponse.<DriverResponse>builder().data(userService.getDriverInfo()).build();
//    }
}
