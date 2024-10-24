package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.dto.food.FoodCollectionResponse;
import com.delivery.delivery_app.entity.FoodCollection;
import com.delivery.delivery_app.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodCollectionRepository extends JpaRepository<FoodCollection, String> {
    @Query("SELECT r FROM Restaurant r JOIN r.merchantCategory mc WHERE mc.foodCollection.id = :foodCollectionId")
    Page<Restaurant> findAllByMerchantCategoryFoodCollectionId(@Param("foodCollectionId") String foodCollectionId, Pageable pageable);

    @Query("SELECT new com.delivery.delivery_app.dto.food.FoodCollectionResponse(fc.id, fc.name, fc.image) FROM FoodCollection fc")
    List<FoodCollectionResponse> findAllFoodCollections();
}
