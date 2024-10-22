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

    Integer price;

    Integer oldPrice;

    String image;

    String name;

    Integer orderCount;

    Integer likeCount;

    List<FoodCustomizeResponse> foodCustomizes;

}
