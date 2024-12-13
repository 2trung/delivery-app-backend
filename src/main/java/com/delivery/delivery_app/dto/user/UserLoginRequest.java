package com.delivery.delivery_app.dto.user;

import com.delivery.delivery_app.validator.ValidPhoneNumber;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {
    @ValidPhoneNumber(message = "INVALID_PHONE_NUMBER")
    String phoneNumber;
    String password;
}
