package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantResponse {
    public RestaurantResponse(String id, String name, String address, String displayAddress, String image, Double latitude, Double longitude, String phoneNumber, Integer reviewCount, Float rating, Float deliveryRadius) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.displayAddress = displayAddress;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.merchantCategoryName = "";
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.deliveryRadius = deliveryRadius;
        this.distance = 0.0;
    }
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

    Double distance;
}
