package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.food.*;
import com.delivery.delivery_app.entity.*;
import com.delivery.delivery_app.mapper.FoodMapper;
import com.delivery.delivery_app.repository.*;
import com.delivery.delivery_app.utils.CustomPageable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    FoodCollectionRepository foodCollectionRepository;

    public List<FoodCollectionResponse> getFoodCollections() {
        return foodCollectionRepository.findAllFoodCollections();
    }

    public Page<RestaurantResponse> getRestaurantByFoodCollection(String id, Pageable pageable) {
        CustomPageable customPageable = new CustomPageable(pageable);
        Page<Restaurant> restaurants = foodCollectionRepository.findAllByMerchantCategoryFoodCollectionId(id, customPageable);
        return foodMapper.toRestaurantResponsePage(restaurants, customPageable);
    }

    public Page<RestaurantResponse> getNearByRestaurant(Double latitude, Double longitude, Pageable pageable) {
        return getNearByRestaurant(latitude, longitude, 5.0, pageable);
    }

    public Page<RestaurantResponse> getNearByRestaurant(Double latitude, Double longitude, Double radius_km, Pageable pageable) {
        CustomPageable customPageable = new CustomPageable(pageable);
        Double latDelta = radius_km / 111.32;
        Double lonDelta = radius_km / (111.32 * Math.cos(Math.toRadians(latitude)));
        Double minLat = latitude - latDelta;
        Double maxLat = latitude + latDelta;
        Double minLon = longitude - lonDelta;
        Double maxLon = longitude + lonDelta;
        Page<RestaurantResponse> restaurants = restaurantRepository.findNearByRestaurant(minLat, maxLat, minLon, maxLon, customPageable);
        for (RestaurantResponse response : restaurants) {
            double distance = haversine(latitude, longitude, response.getLatitude(), response.getLongitude());
            response.setDistance(distance);
        }
        return restaurants;
    }

    public Page<RestaurantResponse> getAllRestaurants(Double latitude, Double longitude,Pageable pageable) {
        CustomPageable customPageable = new CustomPageable(pageable);
        var restaurants = restaurantRepository.getAllBy(customPageable);
        for (RestaurantResponse response : restaurants) {
            double distance = haversine(latitude, longitude, response.getLatitude(), response.getLongitude());
            response.setDistance(distance);
        }
        return restaurants;
    }

    public RestaurantDetailResponse getRestaurantDetail(String id) {
        var objects = restaurantRepository.getRestaurantDetailsById(id);
        return foodMapper.toRestaurantDetailResponse(objects);
    }

    public Page<FoodResponse> getFlashSaleFoods(Pageable pageable) {
        CustomPageable customPageable = new CustomPageable(pageable);
        return foodRepository.getFlashSaleFoods(customPageable);
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

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6371 * c;
    }

    ;
}
