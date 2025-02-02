package com.delivery.delivery_app.dto.user;

import com.delivery.delivery_app.constant.DriverStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    String id;
    UserDataResponse user;
    String licensePlate;
    String vehicleModel;
    String idNumber;
    Double latitude;
    Double longitude;
    Float rating;
    Integer ratingCount;
    Integer orderCount;
    Integer successOrderCount;
    DriverStatus status;
    Instant lastOnline;
}
