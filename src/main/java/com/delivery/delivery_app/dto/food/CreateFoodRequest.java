package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodRequest {
    Integer price;

    Integer old_price;

    String image;

    String name;

    Integer orderCount;

    Integer likeCount;

    String foodCategoryId;
}
