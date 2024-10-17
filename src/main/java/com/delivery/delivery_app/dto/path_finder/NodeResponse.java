package com.delivery.delivery_app.dto.path_finder;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NodeResponse {
    double latitude;
    double longitude;
}
