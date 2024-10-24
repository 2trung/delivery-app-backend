package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.dto.food.RestaurantResponse;
import com.delivery.delivery_app.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    Page<Restaurant> findByMerchantCategoryId(String id, Pageable pageable);

    @Query("SELECT new com.delivery.delivery_app.dto.food.RestaurantResponse(r.id, r.name, r.address, r.displayAddress, r.image, r.latitude, r.longitude, r.phoneNumber, r.reviewCount, r.rating, r.deliveryRadius) FROM Restaurant r WHERE r.latitude >= :minLat AND r.latitude <= :maxLat AND r.longitude >= :minLon AND r.longitude <= :maxLon ORDER BY RANDOM()")
    Page<RestaurantResponse> findNearByRestaurant(Double minLat, Double maxLat, Double minLon, Double maxLon, Pageable pageable);

    @Query("SELECT r, fc, f, fcz, fco " +
            "FROM Restaurant r " +
            "JOIN FoodCategory fc ON fc.restaurant.id = r.id " +
            "JOIN Food f ON fc.id = f.foodCategory.id " +
            "LEFT JOIN FoodCustomize fcz ON f.id = fcz.food.id " +
            "LEFT JOIN FoodCustomizeOption fco ON fcz.id = fco.foodCustomize.id " +
            "WHERE r.id = :restaurantId")
    List<Object[]> getRestaurantDetailsById(@Param("restaurantId") String restaurantId);

    @Query("SELECT new com.delivery.delivery_app.dto.food.RestaurantResponse(r.id, r.name, r.address, r.displayAddress, r.image, r.latitude, r.longitude, r.phoneNumber, r.reviewCount, r.rating, r.deliveryRadius) FROM Restaurant r ORDER BY RANDOM()")
    Page<RestaurantResponse> getAllBy(Pageable pageable);

//    @Query("SELECT new com.delivery.delivery_app.dto.food.RestaurantResponse(r.id, r.name, r.address, r.displayAddress, r.image, r.latitude, r.longitude, r.phoneNumber, r.reviewCount, r.rating, r.deliveryRadius, " +
//            "new com.delivery.delivery_app.dto.food.FoodCategoryResponse(fc.id, fc.name, " +
//            "new com.delivery.delivery_app.dto.food.FoodDetailResponse(f.id, f.name, f.image, f.price, f.oldPrice, f.orderCount, f.likeCount, " +
//            "new com.delivery.delivery_app.dto.food.FoodCustomizeResponse(fcz.id, fcz.name, fcz.minimumChoices, fcz.maximumChoices, " + // Added a comma here
//            "new com.delivery.delivery_app.dto.food.FoodCustomizeOptionResponse(fco.id, fco.name, fco.price, fco.isDefault)))))) " +
//            "FROM Restaurant r " +
//            "JOIN FoodCategory fc ON fc.restaurant.id = r.id " +
//            "JOIN Food f ON fc.id = f.foodCategory.id " +
//            "LEFT JOIN FoodCustomize fcz ON f.id = fcz.food.id " +
//            "LEFT JOIN FoodCustomizeOption fco ON fcz.id = fco.foodCustomize.id " +
//            "WHERE r.id = :restaurantId")
//    RestaurantResponse findRestaurantDetailsById(@Param("restaurantId") String restaurantId);


}
