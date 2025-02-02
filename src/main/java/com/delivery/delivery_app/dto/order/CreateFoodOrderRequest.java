package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodOrderRequest {
    String restaurantId;
    Integer totalFoodCost;
    Integer totalDeliveryCost;
    PaymentMethod paymentMethod;
    List<FoodOrderItem> foodOrderItems;
    CreateOrderLocationRequest deliveryLocation;

    String cardId;

    @Getter
    public static class FoodOrderItem {
        String id;
        Integer quantity;
        String note;
        List<FoodOrderItemCustomize> customizes;
    }

    @Getter
    public static class FoodOrderItemCustomize {
        String id;
        List<String> optionIds;
    }
}
