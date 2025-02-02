package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.dto.order.ChatResponse;
import com.delivery.delivery_app.entity.Message;
import com.delivery.delivery_app.entity.Order;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.mapper.MessageMapper;
import com.delivery.delivery_app.repository.MessageRepository;
import com.delivery.delivery_app.repository.OrderRepository;
import com.delivery.delivery_app.repository.OtpRepository;
import com.delivery.delivery_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    MessageMapper messageMapper;
    SimpMessagingTemplate messagingTemplate;

    public ChatResponse saveMessage(String phoneNumber, String content, String orderId) {
        User sender = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setOrder(order);
//        messagingTemplate.convertAndSend("/topic/chat/" + orderId, messageMapper.toChatResponse(message));
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toChatResponse(savedMessage);
    }

    public List<ChatResponse> getMessages(String orderId) {
        List<Message> messages = messageRepository.findByOrderId(orderId);
        List<ChatResponse> chatResponses = new ArrayList<>();
        for (Message message : messages) {
            chatResponses.add(messageMapper.toChatResponse(message));
        }
        return chatResponses;
    }

}
