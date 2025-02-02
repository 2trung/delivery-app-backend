package com.delivery.delivery_app.dto.order;

import com.delivery.delivery_app.dto.user.UserDataResponse;
import com.delivery.delivery_app.entity.Order;
import com.delivery.delivery_app.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {
    String content;
    String senderId;
    Instant createdAt;
}
