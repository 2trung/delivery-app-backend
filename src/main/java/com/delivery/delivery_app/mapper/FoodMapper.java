package com.delivery.delivery_app.mapper;

import com.delivery.delivery_app.dto.food.*;
import com.delivery.delivery_app.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FoodMapper {


    @Mapping(source = "restaurant.merchantCategory.name", target = "merchantCategoryName")
    RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    default Page<RestaurantResponse> toRestaurantResponsePage(Page<Restaurant> restaurants, Pageable pageable) {
        List<RestaurantResponse> restaurantResponse = restaurants.stream()
                .map(this::toRestaurantResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(restaurantResponse, pageable, restaurants.getTotalElements());
    }


    default RestaurantDetailResponse toRestaurantDetailResponse(List<Object[]> objects) {
        RestaurantDetailResponse restaurantDetailResponse = new RestaurantDetailResponse();
        if (objects.isEmpty()) {
            return restaurantDetailResponse;
        }

        Map<String, FoodCategoryResponse> foodCategoryMap = new HashMap<>();
        Map<String, FoodDetailResponse> foodMap = new HashMap<>();
        Map<String, FoodCustomizeResponse> foodCustomizeMap = new HashMap<>();

        for (Object[] row : objects) {
            Restaurant restaurant = (Restaurant) row[0];
            FoodCategory foodCategory = (FoodCategory) row[1];
            Food food = (Food) row[2];
            FoodCustomize foodCustomize = (FoodCustomize) row[3];
            FoodCustomizeOption foodCustomizeOption = (FoodCustomizeOption) row[4];

            if (restaurantDetailResponse.getId() == null) {
                restaurantDetailResponse.setId(restaurant.getId());
                restaurantDetailResponse.setName(restaurant.getName());
                restaurantDetailResponse.setAddress(restaurant.getAddress());
                restaurantDetailResponse.setDisplayAddress(restaurant.getDisplayAddress());
                restaurantDetailResponse.setImage(restaurant.getImage());
                restaurantDetailResponse.setLatitude(restaurant.getLatitude());
                restaurantDetailResponse.setLongitude(restaurant.getLongitude());
                restaurantDetailResponse.setPhoneNumber(restaurant.getPhoneNumber());
                restaurantDetailResponse.setReviewCount(restaurant.getReviewCount());
                restaurantDetailResponse.setRating(restaurant.getRating());
                restaurantDetailResponse.setDeliveryRadius(restaurant.getDeliveryRadius());
                restaurantDetailResponse.setMerchantCategoryName(restaurant.getMerchantCategory().getName());
            }

            FoodCategoryResponse foodCategoryResponse = foodCategoryMap.computeIfAbsent(foodCategory.getId(), k -> {
                FoodCategoryResponse fcResponse = new FoodCategoryResponse();
                fcResponse.setId(foodCategory.getId());
                fcResponse.setName(foodCategory.getName());
                fcResponse.setFoods(new ArrayList<>());
                return fcResponse;
            });

            FoodDetailResponse foodDetailResponse = foodMap.computeIfAbsent(food.getId(), k -> {
                FoodDetailResponse fResponse = new FoodDetailResponse();
                fResponse.setId(food.getId());
                fResponse.setName(food.getName());
                fResponse.setImage(food.getImage());
                fResponse.setPrice(food.getPrice());
                fResponse.setOldPrice(food.getOldPrice());
                fResponse.setOrderCount(food.getOrderCount());
                fResponse.setLikeCount(food.getLikeCount());
                fResponse.setCustomizes(new ArrayList<>());
                return fResponse;
            });

            if (foodCustomize != null) {
                FoodCustomizeResponse foodCustomizeResponse = foodCustomizeMap.computeIfAbsent(foodCustomize.getId(), k -> {
                    FoodCustomizeResponse fczResponse = new FoodCustomizeResponse();
                    fczResponse.setId(foodCustomize.getId());
                    fczResponse.setName(foodCustomize.getName());
                    fczResponse.setMinimumChoices(foodCustomize.getMinimumChoices());
                    fczResponse.setMaximumChoices(foodCustomize.getMaximumChoices());
                    fczResponse.setOptions(new ArrayList<>());
                    return fczResponse;
                });

                FoodCustomizeOptionResponse foodCustomizeOptionResponse = new FoodCustomizeOptionResponse();
                foodCustomizeOptionResponse.setId(foodCustomizeOption.getId());
                foodCustomizeOptionResponse.setName(foodCustomizeOption.getName());
                foodCustomizeOptionResponse.setPrice(foodCustomizeOption.getPrice());
                foodCustomizeOptionResponse.setIsDefault(foodCustomizeOption.getIsDefault());

                foodCustomizeResponse.getOptions().add(foodCustomizeOptionResponse);
                if (!foodDetailResponse.getCustomizes().contains(foodCustomizeResponse)) {
                    foodDetailResponse.getCustomizes().add(foodCustomizeResponse);
                }
            }

            if (!foodCategoryResponse.getFoods().contains(foodDetailResponse)) {
                foodCategoryResponse.getFoods().add(foodDetailResponse);
            }
        }

        restaurantDetailResponse.setCategories(new ArrayList<>(foodCategoryMap.values()));
        return restaurantDetailResponse;
    }


    Restaurant toRestaurant(CreateRestaurantRequest request);

    FoodCustomize toFoodCustomize(CreateFoodCustomizeRequest request);

    FoodCategory toFoodCategory(CreateFoodCategoryRequest request);
}
