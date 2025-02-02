package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.PaymentMethod;
import com.delivery.delivery_app.constant.ProductCategory;
import com.delivery.delivery_app.constant.ProductSize;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDeliveryOrderRequest {
    Integer cod;

    Integer deliveryCost;

    Double distance;

    ProductSize productSize;

    ProductCategory productCategory;

    PaymentMethod paymentMethod;

    String note;

    List<CreateOrderLocationRequest> locations;

    String senderName;

    String senderPhone;

    String receiverName;

    String receiverPhone;

    String cardId;
}
