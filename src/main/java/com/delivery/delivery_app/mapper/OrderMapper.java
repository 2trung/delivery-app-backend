package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.order.CreateOrderLocationRequest;
import com.delivery.delivery_app.dto.order.OrderLocationResponse;
import com.delivery.delivery_app.dto.order.OrderPreviewResponse;
import com.delivery.delivery_app.dto.order.OrderResponse;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.entity.Food;
import com.delivery.delivery_app.entity.FoodOrderItem;
import com.delivery.delivery_app.entity.Order;
import com.delivery.delivery_app.entity.OrderLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "driver", source = "driver")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "locations", source = "locations")
    @Mapping(source = "locationSequence", target = "locationSequence")
    @Mapping(source = "foodOrderItems", target = "foodOrderItems")
    @Mapping(source = "deliveryOrderDetail", target = "deliveryOrderDetail")
    OrderResponse toOrderResponse(Order order);


    OrderResponse.FoodOrderResponse toFoodResponse(Food food);
    default OrderResponse toFoodOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponse response = toOrderResponse(order);
        response.setFoodOrderItems(order.getFoodOrderItems().stream().map(foodOrderItem -> {
            OrderResponse.FoodOrderItemResponse itemResponse = new OrderResponse.FoodOrderItemResponse();
            itemResponse.setFood(toFoodResponse(foodOrderItem.getFood()));
            itemResponse.setQuantity(foodOrderItem.getQuantity());
            itemResponse.setTotal(foodOrderItem.getTotal());
            itemResponse.setNote(foodOrderItem.getNote());
            itemResponse.setFoodOrderItemCustomizes(foodOrderItem.getFoodOrderItemCustomizes().stream().map(customize -> {
                OrderResponse.FoodOrderItemCustomizeResponse customizeResponse = new OrderResponse.FoodOrderItemCustomizeResponse();
                customizeResponse.setName(customize.getFoodCustomize().getName());
                customizeResponse.setFoodOrderItemCustomizeOptions(customize.getFoodOrderItemCustomizeOptions().stream().map(option -> {
                    OrderResponse.FoodItemCustomizeOptionResponse optionResponse = new OrderResponse.FoodItemCustomizeOptionResponse();
                    optionResponse.setName(option.getFoodCustomizeOption().getName());
                    optionResponse.setPrice(option.getFoodCustomizeOption().getPrice());
                    return optionResponse;
                }).collect(Collectors.toList()));
                return customizeResponse;
            }).collect(Collectors.toList()));
            return itemResponse;
        }).collect(Collectors.toList()));
        return response;
    }

    default OrderPreviewResponse toOrderPreviewResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderPreviewResponse response = new OrderPreviewResponse();
        response.setId(order.getId());
        response.setCost(order.getCost());
        response.setCreatedAt(order.getCreatedAt());
        response.setOrderType(order.getOrderType());
        response.setStatus(order.getStatus());
        response.setLocations(order.getLocations().stream().map(location -> {
            OrderLocationResponse locationResponse = new OrderLocationResponse();
            locationResponse.setLatitude(location.getLatitude());
            locationResponse.setLongitude(location.getLongitude());
            locationResponse.setAddressLine1(location.getAddressLine1());
            locationResponse.setAddressLine2(location.getAddressLine2());
            locationResponse.setSequence(location.getSequence());
            locationResponse.setLocationType(location.getLocationType());
            return locationResponse;
        }).collect(Collectors.toList()));
        response.setFoodOrderItems(order.getFoodOrderItems().stream().map(foodOrderItem -> {
            OrderPreviewResponse.FoodOrderItemResponse itemResponse = new OrderPreviewResponse.FoodOrderItemResponse();
            itemResponse.setFood(foodOrderItem.getFood().getName());
            itemResponse.setQuantity(foodOrderItem.getQuantity());
            itemResponse.setTotal(foodOrderItem.getTotal());
            itemResponse.setNote(foodOrderItem.getNote());
            return itemResponse;
        }).collect(Collectors.toList()));
        return response;
    }

    OrderLocation toOrderLocation(CreateOrderLocationRequest request);


//    default List<OrderResponse.FoodOrderItemCustomizeResponse> mapFoodOrderItemCustomizes(List<FoodOrderItem> foodOrderItems) {
//        if (foodOrderItems == null) {
//            return null;
//        }
//        // Custom logic to map the nested properties
//        return foodOrderItems.stream()
//                .flatMap(item -> item.getFoodOrderItemCustomizes().stream())
//                .map(customize -> {
//                    OrderResponse.FoodOrderItemCustomizeResponse response = new OrderResponse.FoodOrderItemCustomizeResponse();
//                    response.setName(customize.getFoodOrderItem().get);
//                    response.setFoodOrderItemCustomizeOptions(customize.getFoodOrderItemCustomizeOptions().stream()
//                            .map(option -> option.getFoodCustomizeOption().getName())
//                            .collect(Collectors.toList()));
//                    return response;
//                })
//                .collect(Collectors.toList());
//    }

    Order toOrder(OrderResponse orderResponse);
}
