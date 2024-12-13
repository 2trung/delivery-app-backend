package com.delivery.delivery_app.dto.route;

import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.ProductSize;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteFinderRequest {
    OrderType orderType;
    ProductSize productSize;
    Node origin;
    Node destination;
    List<Node> stops;
}
