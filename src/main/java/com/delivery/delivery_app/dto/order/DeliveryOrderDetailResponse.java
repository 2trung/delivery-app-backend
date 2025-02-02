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
public class DeliveryOrderDetailResponse {
    Integer cod;

    Integer deliveryCost;

    ProductSize productSize;

    ProductCategory productCategory;

    String note;

    String senderName;

    String senderPhone;

    String receiverName;

    String receiverPhone;
}
