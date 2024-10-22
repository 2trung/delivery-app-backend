package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.route.RouteFinderRequest;
import com.delivery.delivery_app.dto.route.RouteFinderResponse;
import com.delivery.delivery_app.service.RouteService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RouteController {
    RouteService routeService;

    @PostMapping()
    ApiResponse<RouteFinderResponse> getPath(@RequestBody @Valid RouteFinderRequest request) {
        var result = routeService.findRoute(request);
        return ApiResponse.<RouteFinderResponse>builder().data(result).build();
    }
}
