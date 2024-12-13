package com.delivery.delivery_app.entity;

import com.delivery.delivery_app.constant.DriverStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Driver {
    @Id
    String Id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    User user;

    String licensePlate;

    String vehicleModel;

    String idNumber;

    Double latitude;

    Double longitude;

    Float rating;

    Integer ratingCount;

    Integer orderCount;

    Integer successOrderCount;

    @Enumerated(EnumType.STRING)
    DriverStatus status;

    Instant lastOnline;
}
