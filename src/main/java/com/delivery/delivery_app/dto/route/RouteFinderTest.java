package com.delivery.delivery_app.dto.route;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteFinderTest {
    Node origin;
    Node destination;
}
