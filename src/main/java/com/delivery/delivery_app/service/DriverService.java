package com.delivery.delivery_app.service;

import com.delivery.delivery_app.constant.DriverStatus;
import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.dto.driver.ChangeStatusRequest;
import com.delivery.delivery_app.dto.driver.LocationUpdateRequest;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.entity.Driver;
import com.delivery.delivery_app.entity.Order;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.mapper.UserMapper;
import com.delivery.delivery_app.repository.DriverRepository;
import com.delivery.delivery_app.repository.OrderRepository;
import com.delivery.delivery_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverService {

    UserRepository userRepository;
    DriverRepository driverRepository;
    OrderRepository orderRepository;
    SimpMessagingTemplate messagingTemplate;
    UserMapper userMapper;

    public void confirmOrder(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.CANCELED) {
            User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Driver driver = driverRepository.findById(user.getId()).orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
            driver.setStatus(DriverStatus.BUSY);
            order.setDriver(driver);
            order.setStatus(OrderStatus.IN_PROGRESS);
            driverRepository.save(driver);
            orderRepository.save(order);
            messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.IN_PROGRESS);
            return;
        } else {
            throw new IllegalArgumentException("Đơn hàng đã bị huỷ");
        }
    }

    public void declineOrder(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order not found"));
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Driver driver = driverRepository.findByUserId(user.getId()).orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        driver.setStatus(DriverStatus.ONLINE);
        order.setDriver(null);
        order.setStatus(OrderStatus.PENDING);
        driverRepository.save(driver);
        orderRepository.save(order);
        messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.PENDING);
        return;
    }

    public void pickUpOrder(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order not found"));
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Driver driver = driverRepository.findByUserId(user.getId()).orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        driver.setStatus(DriverStatus.BUSY);
        order.setStatus(OrderStatus.IN_PROGRESS);
        driverRepository.save(driver);
        orderRepository.save(order);
        messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.IN_PROGRESS);
        return;
    }

    public void completeOrder(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order not found"));
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Driver driver = driverRepository.findByUserId(user.getId()).orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        driver.setStatus(DriverStatus.ONLINE);
        order.setStatus(OrderStatus.COMPLETED);
        driverRepository.save(driver);
        orderRepository.save(order);
        messagingTemplate.convertAndSend("/topic/order/" + order.getId(), OrderStatus.COMPLETED);
        return;
    }
    public DriverResponse getDriverDetail(String id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài xế"));
        return userMapper.toDriverResponse(driver);
    }

    public DriverResponse changeStatus(ChangeStatusRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        var user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        Driver driver = driverRepository.findByUserId(user.getId()).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài xế"));
        boolean isOnline = request.getIsOnline();
        if (isOnline) driver.setStatus(DriverStatus.ONLINE);
        else driver.setStatus(DriverStatus.OFFLINE);
        var result = driverRepository.save(driver);
        return userMapper.toDriverResponse(result);
    }

    public void updateLocation(String phoneNumber, LocationUpdateRequest locationUpdateRequest) {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        Driver driver = driverRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        driver.setLatitude(locationUpdateRequest.getLatitude());
        driver.setLongitude(locationUpdateRequest.getLongitude());
        messagingTemplate.convertAndSend("/topic/location/" + driver.getId(), locationUpdateRequest);
        driverRepository.save(driver);
    }


    public List<Driver> getNearbyDrivers(Double latitude, Double longitude) {
        double radius_km = 5.0;
        Double latDelta = radius_km / 111.32;
        Double lonDelta = radius_km / (111.32 * Math.cos(Math.toRadians(latitude)));
        Double minLat = latitude - latDelta;
        Double maxLat = latitude + latDelta;
        Double minLon = longitude - lonDelta;
        Double maxLon = longitude + lonDelta;
        log.info("minLat: {}, maxLat: {}, minLon: {}, maxLon: {}", minLat, maxLat, minLon, maxLon);
        return driverRepository.findNearbyDrivers(minLat, maxLat, minLon, maxLon);
    }
}
