package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.order.ChatRequest;
import com.delivery.delivery_app.dto.order.ChatResponse;
import com.delivery.delivery_app.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    MessageService messageService;

    @MessageMapping("/chat/{orderId}")
    public ChatResponse sendMessage(ChatRequest request, Principal principal, @DestinationVariable String orderId) {
        String phoneNumber = principal.getName();
        String content = request.getContent();
        return messageService.saveMessage(phoneNumber, content, orderId);
    }

    @RequestMapping("/chat/{orderId}")
    public ApiResponse<List<ChatResponse>> getMessages(@PathVariable String orderId) {
        return ApiResponse.<List<ChatResponse>>builder().data(messageService.getMessages(orderId)).build();
    }
}
