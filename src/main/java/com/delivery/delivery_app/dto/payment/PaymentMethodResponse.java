package com.delivery.delivery_app.dto.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMethodResponse {
    String id;
    String brand;
    String last4;
}
