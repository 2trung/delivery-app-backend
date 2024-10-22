package com.delivery.delivery_app.dto.food;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodCustomizeRequest {
    String name;

    Integer limit;

    Integer minimumChoices;

    Integer maximumChoices;

    String foodId;

}
