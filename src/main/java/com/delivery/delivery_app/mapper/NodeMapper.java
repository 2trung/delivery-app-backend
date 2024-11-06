package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.route.Node;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NodeMapper {
    Node toNodeResponse(com.delivery.delivery_app.utils.Node node);
}
