package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.driver.LocationUpdateRequest;
import com.delivery.delivery_app.service.DriverService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverController {
    SimpMessagingTemplate messagingTemplate;
    DriverService driverService;

    //    @SendTo("/topic/location")
    @MessageMapping("/location")
    public void updateLocation(@Payload LocationUpdateRequest locationUpdateRequest,  Principal principal) {
        String phoneNumber = principal.getName();
        driverService.updateLocation(phoneNumber, locationUpdateRequest);
//        return ;
    }

    @MessageMapping("/test")
    public void updateLocation(@Payload String requests) {
        log.info(requests);
    }
}
