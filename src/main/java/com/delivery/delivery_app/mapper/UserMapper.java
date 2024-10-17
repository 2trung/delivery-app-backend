package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.auth.UserDataResponse;
import com.delivery.delivery_app.dto.auth.UserRegisterRequest;
import com.delivery.delivery_app.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterRequest request);

    UserDataResponse toUserDataResponse(User user);
}
