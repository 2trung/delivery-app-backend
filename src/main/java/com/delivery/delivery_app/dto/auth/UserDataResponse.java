package com.delivery.delivery_app.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDataResponse {
    String id;
    String name;
    String phoneNumber;
    String email;
    LocalDate dob;
}
