package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchFoodResponse {
    String id;
    String name;
    String address;
    String displayAddress;
    String image;
    Double latitude;
    Double longitude;
    String phoneNumber;
    Integer reviewCount;
    Float rating;
    List<SearchFood> foods;

    public class SearchFood {
        String id;
        String name;
        String image;
        Integer price;
        Integer oldPrice;
    }

}
