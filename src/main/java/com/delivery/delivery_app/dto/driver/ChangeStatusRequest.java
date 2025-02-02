package com.delivery.delivery_app.dto.driver;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeStatusRequest {
    Boolean isOnline;
}
