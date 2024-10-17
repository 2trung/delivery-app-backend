package com.delivery.delivery_app.dto.path_finder;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteFinderResponse {
    List<NodeResponse> path;
}

