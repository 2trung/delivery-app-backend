package com.delivery.delivery_app.dto.food;

import com.delivery.delivery_app.entity.FoodCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodResponse {
    String id;

    String name;

    String image;

    Integer price;

    Integer oldPrice;

    Integer orderCount;

    Integer likeCount;

    String restaurantId;

    String restaurantName;

    String restaurantAddress;

    Double restaurantLatitude;

    Double restaurantLongitude;
}
