package com.delivery.delivery_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String phoneNumber;

    String otp;

    Instant issuedAt;

    Instant resendAt;

    Instant expiredAt;

    @Column(columnDefinition = "int default 0")
    int retryCount;
}
