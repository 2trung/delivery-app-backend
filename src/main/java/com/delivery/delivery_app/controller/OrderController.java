package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.order.*;
import com.delivery.delivery_app.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;

    @GetMapping()
    ApiResponse<OrderResponse> getOrder(@Param("orderId") String orderId) {
        return ApiResponse.<OrderResponse>builder().data(orderService.getOrder(orderId)).build();
    }

    @PostMapping("/create-ride-order")
    ApiResponse<OrderResponse> createOrder(@RequestBody CreateRideOrderRequest request) {
        return ApiResponse.<OrderResponse>builder().data(orderService.createOrder(request)).build();
    }

    @PostMapping("/create-food-order")
    ApiResponse<OrderResponse> createFoodOrder(@RequestBody CreateFoodOrderRequest request) {
        return ApiResponse.<OrderResponse>builder().data(orderService.createFoodOrder(request)).build();
    }

    @PostMapping("/create-delivery-order")
    ApiResponse<OrderResponse> createDeliveryOrder(@RequestBody CreateDeliveryOrderRequest request) {
        return ApiResponse.<OrderResponse>builder().data(orderService.createDeliveryOrder(request)).build();
    }

    @PutMapping("/{orderId}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable String orderId, @RequestBody UpdateOrderRequest status) {
        return ApiResponse.<OrderResponse>builder().data(orderService.updateOrder(orderId, status)).build();
    }

    @PostMapping("/accept")
    ApiResponse<OrderResponse> acceptOrder(@RequestBody OrderDecisionRequest request) {
        var orderResponse = orderService.acceptOrder(request.getOrderId());
        return ApiResponse.<OrderResponse>builder().data(orderService.acceptOrder(request.getOrderId())).build();
    }

    @PostMapping("/reject")
    ApiResponse<OrderResponse> rejectOrder(@RequestBody OrderDecisionRequest request) {
        return ApiResponse.<OrderResponse>builder().data(orderService.rejectOrder(request.getOrderId())).build();
    }

    @PostMapping("/cancel")
    ApiResponse<OrderResponse> cancelOrder(@RequestBody OrderDecisionRequest request) {
        return ApiResponse.<OrderResponse>builder().data(orderService.cancelOrder(request.getOrderId())).build();
    }

    @GetMapping("/all")
    ApiResponse<Page<OrderPreviewResponse>> getAllOrders(Pageable pageable) {
        return ApiResponse.<Page<OrderPreviewResponse>>builder().data(orderService.getAllOrders(pageable)).build();
    }

    @PostMapping("/{orderId}/confirm-payment")
    ApiResponse<OrderResponse> confirmPayment(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder().data(orderService.confirmPayment(orderId)).build();
    }
}
