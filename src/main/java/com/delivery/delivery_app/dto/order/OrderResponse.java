package com.delivery.delivery_app.dto.order;


import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.PaymentMethod;
import com.delivery.delivery_app.dto.food.FoodCustomizeOptionResponse;
import com.delivery.delivery_app.dto.food.FoodResponse;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.dto.user.UserDataResponse;
import com.delivery.delivery_app.entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;

    DriverResponse driver;

    UserDataResponse user;

    OrderStatus status;

    Instant createdAt;

    Integer cost;

    Double distance;

    PaymentMethod paymentMethod;

    OrderType orderType;

    Integer locationSequence;

    List<OrderLocationResponse> locations;

    List<FoodOrderItemResponse> foodOrderItems;

    DeliveryOrderDetailResponse deliveryOrderDetail;

    String clientSecret;

    @Data
    public static class FoodOrderItemResponse {
        FoodOrderResponse food;
        Integer quantity;
        Integer total;
        String note;
        List<FoodOrderItemCustomizeResponse> foodOrderItemCustomizes;
    }

    @Data
    public static class FoodOrderResponse {
        Integer price;
        Integer oldPrice;
        String image;
        String name;
        Integer orderCount;
        Integer likeCount;
    }

    @Data
    public static class FoodOrderItemCustomizeResponse {
        String name;
        List<FoodItemCustomizeOptionResponse> foodOrderItemCustomizeOptions;
    }

    @Data
    public static class FoodItemCustomizeOptionResponse {
        String name;
        Integer price;
    }
}
