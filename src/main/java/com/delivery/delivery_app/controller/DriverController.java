package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.driver.ChangeStatusRequest;
import com.delivery.delivery_app.dto.driver.LocationUpdateRequest;
import com.delivery.delivery_app.dto.user.DriverResponse;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.service.DriverService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverController {
    SimpMessagingTemplate messagingTemplate;
    DriverService driverService;
    //    @SendTo("/topic/location")
    @PutMapping("/status")
    ApiResponse<DriverResponse> changeStatus(@RequestBody ChangeStatusRequest request) {
        return ApiResponse.<DriverResponse>builder().data(driverService.changeStatus(request)).build();
    }
    @GetMapping()
    ApiResponse<DriverResponse> getDriverDetail(@RequestParam String id) {
        return ApiResponse.<DriverResponse>builder().data(driverService.getDriverDetail(id)).build();
    }
    @MessageMapping("/location")
    void updateLocation(@Payload LocationUpdateRequest locationUpdateRequest,  Principal principal) {
        String phoneNumber = principal.getName();
        driverService.updateLocation(phoneNumber, locationUpdateRequest);
    }
}
