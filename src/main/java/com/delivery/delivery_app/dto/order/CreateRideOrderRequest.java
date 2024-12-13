package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.PaymentMethod;
import com.delivery.delivery_app.entity.OrderLocation;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRideOrderRequest {
    Integer cost;

    Double distance;

    PaymentMethod paymentMethod;

    String cardId;

    List<CreateOrderLocationRequest> locations;
}
