package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.dto.order.CreateRideOrderRequest;
import com.delivery.delivery_app.dto.route.Node;
import com.delivery.delivery_app.dto.route.RouteFinderRequest;
import com.delivery.delivery_app.dto.route.RouteResponse;
import com.delivery.delivery_app.entity.Driver;
import com.delivery.delivery_app.entity.Order;
import com.delivery.delivery_app.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    DriverService driverService;
    SimpMessagingTemplate messagingTemplate;
    RouteService routeService;
    OrderRepository orderRepository;
    Map<String, ScheduledExecutorService> searchSchedulers = new ConcurrentHashMap<>();

    public String createOrder(CreateRideOrderRequest request) {
        RouteFinderRequest routeFinderRequest = RouteFinderRequest.builder().origin(Node.builder().latitude(request.getOriginLatitude()).longitude(request.getOriginLongitude()).build()).destination(Node.builder().latitude(request.getDestinationLatitude()).longitude(request.getDestinationLongitude()).build()).build();
        RouteResponse route = routeService.findRoute(routeFinderRequest);
        if (!Objects.equals(route.getCost(), request.getCost()) || !Objects.equals(route.getDistance(), request.getDistance())) {
            return "Invalid cost or distance";
        }
        Order order = Order.builder().originLatitude(request.getOriginLatitude()).originLongitude(request.getOriginLongitude()).destinationLatitude(request.getDestinationLatitude()).destinationLongitude(request.getDestinationLongitude()).cost(request.getCost()).distance(request.getDistance()).paymentMethod(request.getPaymentMethod()).status(OrderStatus.PENDING).build();
        Order savedOrder = orderRepository.save(order);
        searchDriverSchedule(savedOrder);
        return "Order created successfully";
    }

    public void searchDriverSchedule(Order order) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        searchSchedulers.put(order.getId(), scheduler);
        scheduler.scheduleAtFixedRate(() -> {
            boolean foundDriver = searchDriver(order.getId());
            if (foundDriver) stopDriverSearch(order.getId());
        }, 0, 30, TimeUnit.SECONDS);
        scheduler.schedule(() -> {
            stopDriverSearch(order.getId());
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.CANCELED);
        }, 15, TimeUnit.MINUTES);
    }

    public boolean searchDriver(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.PENDING && Objects.isNull(order.getDriver())) {
            List<Driver> drivers = driverService.getNearbyDrivers(order.getOriginLatitude(), order.getOriginLongitude());
            if (!drivers.isEmpty()) {
                Random random = new Random();
                Driver driver = drivers.get(random.nextInt(drivers.size()));
                order.setStatus(OrderStatus.WAITING_FOR_ACCEPTANCE);
                messagingTemplate.convertAndSend("/topic/driver/" + driver.getUser().getId(), order.getId());
                orderRepository.save(order);
            }
            return false;
        } else {
            return true;
        }
    }

    public void stopDriverSearch(String orderId) {
        ScheduledExecutorService scheduler = searchSchedulers.remove(orderId);
        if (scheduler != null) scheduler.shutdown();
    }
}
