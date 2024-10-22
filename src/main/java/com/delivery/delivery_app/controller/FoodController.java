package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.food.*;
import com.delivery.delivery_app.entity.*;
import com.delivery.delivery_app.service.FoodService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodController {
    private final FoodService foodService;

    @GetMapping("/collection")
    ApiResponse<Page<FoodCollectionResponse>> getFoodCollection(@RequestParam String id, @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<Page<FoodCollectionResponse>>builder().data(foodService.getFoodCollection(id, pageable)).build();
    }

    @GetMapping("/restaurant")
    ApiResponse<RestaurantDetailResponse> getRestaurantDetail(@RequestParam String id) {
        return ApiResponse.<RestaurantDetailResponse>builder().data(foodService.getRestaurantDetail(id)).build();
    }

    @PostMapping("/create-restaurant")
    ApiResponse<Restaurant> createRestaurant(@RequestBody CreateRestaurantRequest request) {
        return ApiResponse.<Restaurant>builder().data(foodService.createRestaurant(request)).build();
    }

    @PostMapping("/create-category")
    ApiResponse<FoodCategory> createFoodCategory(@RequestBody CreateFoodCategoryRequest request) {
        return ApiResponse.<FoodCategory>builder().data(foodService.createFoodCategory(request)).build();
    }

    @PostMapping("/create-customize")
    ApiResponse<FoodCustomize> createFoodCustomize(@RequestBody CreateFoodCustomizeRequest request) {
        return ApiResponse.<FoodCustomize>builder().data(foodService.createFoodCustomize(request)).build();
    }

    @PostMapping("/create-customize-option")
    ApiResponse<FoodCustomizeOption> createFoodCustomizeOption(@RequestBody CreateFoodCustomizeOptionRequest request) {
        return ApiResponse.<FoodCustomizeOption>builder().data(foodService.createFoodCustomizeOption(request)).build();
    }

    @PostMapping("/create")
    ApiResponse<Food> createFood(@RequestBody CreateFoodRequest request) {
        return ApiResponse.<Food>builder().data(foodService.createFood(request)).build();
    }
}
