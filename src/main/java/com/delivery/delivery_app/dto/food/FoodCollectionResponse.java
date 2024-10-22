package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodCollectionResponse {
    String id;

    String name;

    String address;

    String displayAddress;

    String image;

    Double latitude;

    Double longitude;

    String phoneNumber;

    String merchantCategoryName;

    Integer reviewCount;

    Float rating;

    Float deliveryRadius;
}
