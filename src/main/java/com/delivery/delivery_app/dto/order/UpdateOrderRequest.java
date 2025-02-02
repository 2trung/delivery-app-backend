package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderRequest {
    OrderStatus status;

    Integer locationSequence;
}
