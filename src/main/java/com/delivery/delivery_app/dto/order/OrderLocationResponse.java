package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.constant.LocationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderLocationResponse {
    String addressLine1;

    String addressLine2;

    Double latitude;

    Double longitude;

    LocationType locationType;

    Integer sequence;
}
