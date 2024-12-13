package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.dto.user.UserDataResponse;
import com.delivery.delivery_app.dto.user.UserRegisterRequest;
import com.delivery.delivery_app.entity.Driver;
import com.delivery.delivery_app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterRequest request);
    UserDataResponse toUserDataResponse(User user);

    @Mapping(target = "user", source = "user")
    DriverResponse toDriverResponse(Driver driver);
}
