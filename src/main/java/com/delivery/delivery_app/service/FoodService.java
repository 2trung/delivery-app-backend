package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.food.*;
import com.delivery.delivery_app.entity.*;
import com.delivery.delivery_app.mapper.FoodMapper;
import com.delivery.delivery_app.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodService {
    FoodMapper foodMapper;
    RestaurantRepository restaurantRepository;
    FoodCustomizeRepository foodCustomizeRepository;
    FoodCustomizeOptionRepository foodCustomizeOptionRepository;
    FoodCategoryRepository foodCategoryRepository;
    FoodRepository foodRepository;

    public Page<FoodCollectionResponse> getFoodCollection(String id, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findByMerchantCategoryId(id, pageable);
        return foodMapper.toRestaurantResponsePage(restaurants, pageable);
    }

    @Transactional
    public RestaurantDetailResponse getRestaurantDetail(String id) {
        var restaurant = restaurantRepository.findById(id).orElseThrow();
        return foodMapper.toRestaurantDetailResponse(restaurant);
    }

    public Restaurant createRestaurant(CreateRestaurantRequest request) {
        return restaurantRepository.save(foodMapper.toRestaurant(request));
    }

    public FoodCategory createFoodCategory(CreateFoodCategoryRequest request) {
        return foodCategoryRepository.save(foodMapper.toFoodCategory(request));
    }

    public FoodCustomize createFoodCustomize(CreateFoodCustomizeRequest request) {
        Food food = foodRepository.findById(request.getFoodId()).orElseThrow();
        FoodCustomize foodCustomize = FoodCustomize.builder()
                .name(request.getName())
                .maximumChoices(request.getMaximumChoices())
                .minimumChoices(request.getMinimumChoices())
                .food(food)
                .build();
        return foodCustomizeRepository.save(foodCustomize);
    }

    public FoodCustomizeOption createFoodCustomizeOption(CreateFoodCustomizeOptionRequest request) {
        FoodCustomize foodCustomize = foodCustomizeRepository.findById(request.getFoodCustomizeId()).orElseThrow();
        FoodCustomizeOption foodCustomizeOption = FoodCustomizeOption.builder()
                .name(request.getName())
                .price(request.getPrice())
                .isDefault(request.getIsDefault())
                .foodCustomize(foodCustomize)
                .build();
        return foodCustomizeOptionRepository.save(foodCustomizeOption);
    }

    public Food createFood(CreateFoodRequest request) {
        FoodCategory foodCategory = foodCategoryRepository.findById(request.getFoodCategoryId()).orElseThrow();
        Food food = Food.builder()
                .price(request.getPrice())
                .oldPrice(request.getOld_price())
                .image(request.getImage())
                .name(request.getName())
                .orderCount(request.getOrderCount())
                .likeCount(request.getLikeCount())
                .foodCategory(foodCategory)
                .build();
        return foodRepository.save(food);
    }
}
