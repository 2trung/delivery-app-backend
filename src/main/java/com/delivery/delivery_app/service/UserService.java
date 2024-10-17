package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.auth.UserDataResponse;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.exception.AppException;
import com.delivery.delivery_app.exception.ErrorCode;
import com.delivery.delivery_app.mapper.UserMapper;
import com.delivery.delivery_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserDataResponse getUserInfo() {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIAL));
        return userMapper.toUserDataResponse(user);
    }
}
