package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.food.*;
import com.delivery.delivery_app.entity.FoodCategory;
import com.delivery.delivery_app.entity.FoodCustomize;
import com.delivery.delivery_app.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FoodMapper {


    @Mapping(source = "restaurant.merchantCategory.name", target = "merchantCategoryName")
    FoodCollectionResponse toRestaurantResponse(Restaurant restaurant);

    default Page<FoodCollectionResponse> toRestaurantResponsePage(Page<Restaurant> restaurants, Pageable pageable) {
        List<FoodCollectionResponse> foodCollectionResponse = restaurants.stream()
                .map(this::toRestaurantResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(foodCollectionResponse, pageable, restaurants.getTotalElements());
    }

    @Mapping(source = "restaurant.merchantCategory.name", target = "merchantCategoryName")
    @Mapping(source = "restaurant.foodCategories", target = "categories")
    RestaurantDetailResponse toRestaurantDetailResponse(Restaurant restaurant);

    Restaurant toRestaurant(CreateRestaurantRequest request);

    FoodCustomize toFoodCustomize(CreateFoodCustomizeRequest request);

    FoodCategory toFoodCategory(CreateFoodCategoryRequest request);
}
