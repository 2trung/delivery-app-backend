package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.path_finder.NodeResponse;
import com.delivery.delivery_app.utils.Node;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NodeMapper {
    NodeResponse toNodeResponse(Node node);
}
