package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.path_finder.RouteFinderResponse;
import com.delivery.delivery_app.service.RouteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RouteController {
    RouteService routeService;

    @GetMapping()
    ApiResponse<RouteFinderResponse> getPath(@Param("source") String source, @Param("destination") String destination) {
        var result = routeService.findRoute(source, destination);
        log.info(source, destination);
        return ApiResponse.<RouteFinderResponse>builder().data(result).build();
    }
}
