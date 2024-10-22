package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodCustomizeOptionResponse {
    String id;

    String name;

    Integer price;

    Boolean isDefault;
}
