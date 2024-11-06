package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRideOrderRequest {
    Integer cost;

    Double distance;

    PaymentMethod paymentMethod;

    String originAddress;

    Double originLatitude;

    Double originLongitude;

    String destinationAddress;

    Double destinationLatitude;

    Double destinationLongitude;
}
