package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodCustomizeResponse {

    String id;

    String name;

    Integer minimumChoices;

    Integer maximumChoices;

    List<FoodCustomizeOptionResponse> options;
}
