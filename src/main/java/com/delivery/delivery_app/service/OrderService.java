package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.*;
import com.delivery.delivery_app.dto.order.*;
import com.delivery.delivery_app.dto.route.RouteFinderRequest;
import com.delivery.delivery_app.dto.route.RouteResponse;
import com.delivery.delivery_app.entity.*;
import com.delivery.delivery_app.mapper.OrderMapper;
import com.delivery.delivery_app.repository.*;
import com.delivery.delivery_app.dto.route.Node;
import com.delivery.delivery_app.utils.CustomPageable;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    DriverService driverService;
    StripeService stripeService;
    SimpMessagingTemplate messagingTemplate;
    RouteService routeService;
    OrderRepository orderRepository;
    Map<String, ScheduledExecutorService> searchSchedulers = new ConcurrentHashMap<>();
    OrderMapper orderMapper;
    DriverRepository driverRepository;
    OrderLocationRepository orderLocationRepository;
    FoodRepository foodRepository;
    FoodCustomizeRepository foodCustomizeRepository;
    FoodCustomizeOptionRepository foodCustomizeOptionRepository;
    RestaurantRepository restaurantRepository;

    public OrderResponse getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return orderMapper.toFoodOrderResponse(order);
    }

    public OrderResponse createOrder(CreateRideOrderRequest request) {
        List<Node> nodes = request.getLocations().stream().sorted(Comparator.comparingInt(CreateOrderLocationRequest::getSequence)).map(location -> Node.builder().latitude(location.getLatitude()).longitude(location.getLongitude()).build()).collect(Collectors.toList());
        RouteFinderRequest routeFinderRequest = RouteFinderRequest.builder().origin(nodes.getFirst()).destination(nodes.getLast()).stops(new ArrayList<>()).build();
        routeFinderRequest.setOrderType(OrderType.RIDE);
        for (int i = 1; i < nodes.size() - 1; i++) {
            routeFinderRequest.getStops().add(nodes.get(i));
        }
        RouteResponse route = routeService.findRoute(routeFinderRequest);
        if (!Objects.equals(route.getCost(), request.getCost()) || !Objects.equals(route.getDistance(), request.getDistance())) {
            throw new IllegalArgumentException("Dữ liệu đã được cập nhật, vui lòng thử lại!");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = Order.builder().cost(request.getCost()).distance(request.getDistance()).paymentMethod(request.getPaymentMethod()).orderType(OrderType.RIDE).user(user).locationSequence(1).locations(new ArrayList<>()).build();
        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        else order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        for (CreateOrderLocationRequest locationDto : request.getLocations()) {
            OrderLocation location = OrderLocation.builder().addressLine1(locationDto.getAddressLine1()).addressLine2(locationDto.getAddressLine2()).latitude(locationDto.getLatitude()).longitude(locationDto.getLongitude()).sequence(locationDto.getSequence()).locationType(locationDto.getLocationType()).order(savedOrder).build();
            var savedLocation = orderLocationRepository.save(location);
            savedOrder.getLocations().add(savedLocation);
        }
        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
            if (user.getPaymentId() == null)
                throw new IllegalArgumentException("Vui lòng thêm phương thức thanh toán trước khi đặt hàng");
            try {
                PaymentIntent paymentIntent = stripeService.paymentIntent(user.getPaymentId(), request.getCardId(), Long.valueOf(request.getCost()));
                if (paymentIntent.getStatus().equals("succeeded")) {
                    savedOrder.setStatus(OrderStatus.PENDING);
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                } else if (paymentIntent.getStatus().equals("requires_action")) {
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                    return OrderResponse.builder().id(savedOrder.getId()).clientSecret(paymentIntent.getClientSecret()).build();
                } else {
                    throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
                }
            } catch (StripeException e) {
                throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
            }
        }

        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);
        searchDriverSchedule(savedOrder);
        return orderResponse;
    }

    public OrderResponse confirmPayment(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getPaymentIntentId().isEmpty())
            throw new IllegalArgumentException("Đơn hàng không yêu cầu thanh toán");
        try {
            PaymentIntent paymentIntent = stripeService.verifyPayment(order.getPaymentIntentId());
            if (paymentIntent.getStatus().equals("succeeded")) {
                order.setStatus(OrderStatus.PENDING);
                Order savedOrder = orderRepository.save(order);
                searchDriverSchedule(savedOrder);
                return orderMapper.toOrderResponse(savedOrder);
            } else {
                throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
            }
        } catch (StripeException e) {
            throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
        }

    }

    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getStatus() == OrderStatus.IN_PROGRESS || order.getStatus() == OrderStatus.ARRIVING) {
            order.setLocationSequence(request.getLocationSequence());
            if (request.getStatus() != order.getStatus()) {
                order.setStatus(request.getStatus());
                Order savedOrder = orderRepository.save(order);
                messagingTemplate.convertAndSend("/topic/order/" + order.getId(), request.getStatus());
                if (request.getStatus() == OrderStatus.COMPLETED) {
                    order.getDriver().setStatus(DriverStatus.ONLINE);
                    driverRepository.save(order.getDriver());
                }
                return orderMapper.toOrderResponse(savedOrder);
            } else {
                Order savedOrder = orderRepository.save(order);
                messagingTemplate.convertAndSend("/topic/order/" + order.getId(), request.getLocationSequence());
                return orderMapper.toOrderResponse(savedOrder);
            }
        }
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse createFoodOrder(CreateFoodOrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        RouteFinderRequest routeFinderRequest = RouteFinderRequest.builder().origin(Node.builder().latitude(restaurant.getLatitude()).longitude(restaurant.getLongitude()).build()).destination(Node.builder().latitude(request.getDeliveryLocation().getLatitude()).longitude(request.getDeliveryLocation().getLongitude()).build()).stops(new ArrayList<>()).build();
        routeFinderRequest.setOrderType(OrderType.FOOD_DELIVERY);
        RouteResponse route = routeService.findRoute(routeFinderRequest);
        if (!Objects.equals(route.getCost(), request.getTotalDeliveryCost())) {
            throw new IllegalArgumentException("Dữ liệu đã được cập nhật, vui lòng thử lại!");
        }
        Order order = new Order();
        order.setFoodOrderItems(new ArrayList<>());
        order.setLocations(new ArrayList<>());
        Integer foodCost = 0;
        for (var foodRequest : request.getFoodOrderItems()) {
            Integer cost = 0;
            Food food = foodRepository.findById(foodRequest.getId()).orElseThrow(() -> new IllegalArgumentException("Food not found"));
            FoodOrderItem foodOrderItem = new FoodOrderItem();
            foodOrderItem.setFood(food);
            foodOrderItem.setQuantity(foodRequest.getQuantity());
            foodOrderItem.setOrder(order);
            foodOrderItem.setNote(foodRequest.getNote());
            foodOrderItem.setFoodOrderItemCustomizes(new ArrayList<>());
            cost += food.getPrice();

            for (var foodCustomizeRequest : foodRequest.getCustomizes()) {
                FoodCustomize foodCustomize = foodCustomizeRepository.findById(foodCustomizeRequest.getId()).orElseThrow(() -> new IllegalArgumentException("Customize not found"));
                if (!Objects.equals(foodCustomize.getFood().getId(), food.getId()))
                    throw new IllegalArgumentException("Customize not found");
                FoodOrderItemCustomize foodOrderItemCustomize = new FoodOrderItemCustomize();
                foodOrderItemCustomize.setFoodCustomize(foodCustomize);
                foodOrderItemCustomize.setFoodOrderItem(foodOrderItem);
                foodOrderItemCustomize.setFoodOrderItemCustomizeOptions(new ArrayList<>());

                for (var foodCustomizeOptionId : foodCustomizeRequest.getOptionIds()) {
                    FoodCustomizeOption foodCustomizeOption = foodCustomizeOptionRepository.findById(foodCustomizeOptionId).orElseThrow(() -> new IllegalArgumentException("Customize option not found"));
                    if (!Objects.equals(foodCustomizeOption.getFoodCustomize().getId(), foodCustomize.getId()))
                        throw new IllegalArgumentException("Customize option not found");
                    FoodOrderItemCustomizeOption foodOrderItemCustomizeOption = new FoodOrderItemCustomizeOption();
                    foodOrderItemCustomizeOption.setFoodCustomizeOption(foodCustomizeOption);
                    foodOrderItemCustomizeOption.setFoodOrderItemCustomize(foodOrderItemCustomize);
                    foodOrderItemCustomize.getFoodOrderItemCustomizeOptions().add(foodOrderItemCustomizeOption);
                    cost += foodCustomizeOption.getPrice();
                }
                foodOrderItem.getFoodOrderItemCustomizes().add(foodOrderItemCustomize);
            }
            cost *= foodRequest.getQuantity();
            foodOrderItem.setTotal(cost);
            order.getFoodOrderItems().add(foodOrderItem);
            foodCost += cost;
        }
//        if (!cost.equals(request.getTotalFoodCost())) throw new IllegalArgumentException("Giá đã được cập nhật, vui lòng thử lại!");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderLocation restaurantLocation = OrderLocation.builder().latitude(restaurant.getLatitude()).longitude(restaurant.getLongitude()).addressLine1(restaurant.getName()).addressLine2(restaurant.getAddress()).locationType(LocationType.PICKUP).sequence(1).order(order).build();
        OrderLocation deliveryLocation = OrderLocation.builder().latitude(request.getDeliveryLocation().getLatitude()).longitude(request.getDeliveryLocation().getLongitude()).addressLine1(request.getDeliveryLocation().getAddressLine1()).addressLine2(request.getDeliveryLocation().getAddressLine2()).locationType(LocationType.DROPOFF).sequence(2).order(order).build();
        order.getLocations().add(restaurantLocation);
        order.getLocations().add(deliveryLocation);
        order.setUser(user);
        order.setCost(foodCost + route.getCost());
        order.setDistance(route.getDistance());
        order.setLocationSequence(1);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderType(OrderType.FOOD_DELIVERY);
        order.setFoodOrderItems(order.getFoodOrderItems());
        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        else order.setStatus(OrderStatus.PENDING);
        var savedOrder = orderRepository.save(order);

        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
            if (user.getPaymentId() == null)
                throw new IllegalArgumentException("Vui lòng thêm phương thức thanh toán trước khi đặt hàng");
            try {
                PaymentIntent paymentIntent = stripeService.paymentIntent(user.getPaymentId(), request.getCardId(), Long.valueOf(request.getTotalFoodCost() + request.getTotalDeliveryCost()));
                if (paymentIntent.getStatus().equals("succeeded")) {
                    savedOrder.setStatus(OrderStatus.PENDING);
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                } else if (paymentIntent.getStatus().equals("requires_action")) {
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                    return OrderResponse.builder().id(savedOrder.getId()).clientSecret(paymentIntent.getClientSecret()).build();
                } else {
                    throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
                }
            } catch (StripeException e) {
                throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
            }
        }
        searchDriverSchedule(savedOrder);
        return orderMapper.toFoodOrderResponse(savedOrder);
    }

    public OrderResponse createDeliveryOrder(CreateDeliveryOrderRequest request) {
        List<Node> nodes = request.getLocations().stream().sorted(Comparator.comparingInt(CreateOrderLocationRequest::getSequence)).map(location -> Node.builder().latitude(location.getLatitude()).longitude(location.getLongitude()).build()).collect(Collectors.toList());
        RouteFinderRequest routeFinderRequest = RouteFinderRequest.builder().origin(nodes.getFirst()).destination(nodes.getLast()).stops(new ArrayList<>()).build();
        routeFinderRequest.setOrderType(OrderType.DELIVERY);
        routeFinderRequest.setProductSize(request.getProductSize());

        RouteResponse route = routeService.findRoute(routeFinderRequest);
        if (!Objects.equals(route.getCost(), request.getDeliveryCost()) || !Objects.equals(route.getDistance(), request.getDistance())) {
            throw new IllegalArgumentException("Dữ liệu đã được cập nhật, vui lòng thử lại!");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderLocation> locations = request.getLocations().stream().map(orderMapper::toOrderLocation).collect(Collectors.toList());
        Order order = new Order();
        DeliveryOrderDetail deliveryOrderDetail = DeliveryOrderDetail.builder().senderName(request.getSenderName()).senderPhone(request.getSenderPhone()).receiverName(request.getReceiverName()).receiverPhone(request.getReceiverPhone()).productSize(request.getProductSize()).productCategory(request.getProductCategory()).deliveryCost(request.getDeliveryCost()).cod(request.getCod()).note(request.getNote()).build();

        order.setCost(request.getCod() + request.getDeliveryCost());
        order.setDistance(request.getDistance());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderType(OrderType.DELIVERY);
        order.setUser(user);
        order.setLocationSequence(1);
        order.setLocations(locations);
        order.setDeliveryOrderDetail(deliveryOrderDetail);
        deliveryOrderDetail.setOrder(order);
        locations.forEach(location -> {
            if (location.getSequence() == 1) location.setLocationType(LocationType.PICKUP);
            else if (location.getSequence() == 2) location.setLocationType(LocationType.DROPOFF);
            location.setOrder(order);
        });
        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        else order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
            if (user.getPaymentId() == null)
                throw new IllegalArgumentException("Vui lòng thêm phương thức thanh toán trước khi đặt hàng");
            try {
                PaymentIntent paymentIntent = stripeService.paymentIntent(user.getPaymentId(), request.getCardId(), Long.valueOf(request.getDeliveryCost()));
                if (paymentIntent.getStatus().equals("succeeded")) {
                    savedOrder.setStatus(OrderStatus.PENDING);
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                } else if (paymentIntent.getStatus().equals("requires_action")) {
                    savedOrder.setPaymentIntentId(paymentIntent.getId());
                    orderRepository.save(savedOrder);
                    return OrderResponse.builder().id(savedOrder.getId()).clientSecret(paymentIntent.getClientSecret()).build();
                } else {
                    throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
                }
            } catch (StripeException e) {
                throw new IllegalArgumentException("Thanh toán thất bại, vui lòng thử lại");
            }
        }

        searchDriverSchedule(savedOrder);
        return orderMapper.toOrderResponse(savedOrder);
    }


    public OrderResponse acceptOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getStatus() == OrderStatus.WAITING_FOR_ACCEPTANCE) {
            order.setStatus(OrderStatus.ARRIVING);
            orderRepository.save(order);
            messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.ARRIVING);
        }
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse rejectOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getStatus() == OrderStatus.WAITING_FOR_ACCEPTANCE) {
            order.setStatus(OrderStatus.PENDING);
            order.getDriver().setStatus(DriverStatus.ONLINE);
            orderRepository.save(order);
            driverRepository.save(order.getDriver());
            messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.PENDING);
        }
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse cancelOrder(String orderId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (Objects.equals(order.getUser().getId(), user.getId())) {
            if (order.getStatus() != OrderStatus.PENDING) throw new IllegalArgumentException("Không thể huỷ đơn hàng");
            else {
                order.setStatus(OrderStatus.CANCELED);
                var savedOrder = orderRepository.save(order);
                messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.CANCELED);
                try {
                    if (order.getPaymentMethod() == PaymentMethod.CREDIT_CARD && order.getPaymentIntentId() != null && order.getStatus() != OrderStatus.WAITING_FOR_PAYMENT)
                        stripeService.refundPayment(order.getPaymentIntentId());
                } catch (StripeException e) {
                    log.info("Refund failed");
                }
                return orderMapper.toOrderResponse(savedOrder);
            }
        }
        if (Objects.equals(order.getDriver().getUser().getId(), user.getId())) {
            if (order.getStatus() == OrderStatus.IN_PROGRESS)
                throw new IllegalArgumentException("Không thể huỷ đơn hàng");
            else {
                order.setStatus(OrderStatus.CANCELED);
                order.getDriver().setStatus(DriverStatus.ONLINE);
                var savedOrder = orderRepository.save(order);
                driverRepository.save(order.getDriver());
                messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.CANCELED);
                return orderMapper.toOrderResponse(savedOrder);
            }
        }
        throw new IllegalArgumentException("Không thể huỷ đơn hàng");
    }

    public void searchDriverSchedule(Order order) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        searchSchedulers.put(order.getId(), scheduler);
        AtomicBoolean isSearchStopped = new AtomicBoolean(false);
        scheduler.scheduleAtFixedRate(() -> {
            if (isSearchStopped.get()) return;
            boolean foundDriver = searchDriver(order.getId());
            if (foundDriver) {
                stopDriverSearch(order.getId());
                isSearchStopped.set(true);
            }
        }, 0, 30, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            if (isSearchStopped.get()) return;
            stopDriverSearch(order.getId());
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.CANCELED);
        }, 15, TimeUnit.MINUTES);
    }

    public boolean searchDriver(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.PENDING) {
            var pickupLocation = order.getLocations().stream().filter(location -> location.getLocationType().equals(LocationType.PICKUP)).findFirst().orElseThrow(() -> new IllegalArgumentException("Pickup location not found"));
            List<Driver> drivers = driverService.getNearbyDrivers(pickupLocation.getLatitude(), pickupLocation.getLongitude());
            if (order.getDriver() != null) drivers.removeIf(d -> d.getId().equals(order.getDriver().getId()));
            if (!drivers.isEmpty()) {
                Random random = new Random();
                Driver driver = drivers.get(random.nextInt(drivers.size()));
                log.info("Driver found for order " + orderId + " with driver " + driver.getId());
                order.setStatus(OrderStatus.WAITING_FOR_ACCEPTANCE);
                order.setDriver(driver);
                driver.setStatus(DriverStatus.BUSY);
                messagingTemplate.convertAndSend("/topic/driver/" + driver.getId(), order.getId());
                orderRepository.save(order);
                driverRepository.save(driver);
            }
            return false;
        } else if (order.getStatus() == OrderStatus.WAITING_FOR_ACCEPTANCE && Objects.nonNull(order.getDriver())) {
            Duration timeSinceLastUpdate = Duration.between(order.getUpdatedAt(), Instant.now());
            log.info(String.valueOf(timeSinceLastUpdate.getSeconds()));
            Driver driver = order.getDriver();
            if (timeSinceLastUpdate.getSeconds() >= 29) {
                order.setStatus(OrderStatus.PENDING);
                driver.setStatus(DriverStatus.ONLINE);
                orderRepository.save(order);
                driverRepository.save(driver);
                messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.PENDING);
            }
            return false;
        } else {
            log.info("Driver found for order " + orderId);
            return true;
        }
    }

    public void stopDriverSearch(String orderId) {
        ScheduledExecutorService scheduler = searchSchedulers.remove(orderId);
        if (scheduler != null) scheduler.shutdown();
    }

    public Page<OrderPreviewResponse> getAllOrders(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomPageable customPageable = new CustomPageable(pageable);
        Page<Order> orders = orderRepository.findAllOrdersByUserId(user.getId(), customPageable);
        return orders.map(orderMapper::toOrderPreviewResponse);
    }
}
