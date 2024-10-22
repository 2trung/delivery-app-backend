package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.dto.food.RestaurantDetailResponse;
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

    //    @Query("SELECT r FROM Restaurant r " +
//            "JOIN r.foodCategory fc " +
//            "JOIN fc.items i " +
//            "JOIN i.customizeItem ci " +
//            "JOIN ci.options o " +
//            "WHERE r.id = :id")
//    @Query("SELECT r FROM Restaurant r JOIN r.foodCategory WHERE r.id = :id")
//    Restaurant getRestaurantWithDetails(String id);

}
