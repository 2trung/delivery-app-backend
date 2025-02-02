package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.order.ChatResponse;
import com.delivery.delivery_app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "senderId", source = "sender.id")
    ChatResponse toChatResponse(Message message);
    @Mapping(target = "senderId", source = "sender.id")
    List<ChatResponse> toChatResponseList(List<Message> messages);
}
