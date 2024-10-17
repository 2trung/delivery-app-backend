package com.delivery.delivery_app.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InputPhoneNumberResponse {
    boolean isExistingUser;
    String nextAction;
}
