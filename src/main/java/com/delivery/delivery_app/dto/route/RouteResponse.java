package com.delivery.delivery_app.dto.route;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteResponse {
    List<Node> path;
    Double distance;
    String duration;
    Integer cost;
}



