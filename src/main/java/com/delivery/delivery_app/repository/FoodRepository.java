package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.dto.food.FoodResponse;
import com.delivery.delivery_app.entity.Food;
import com.delivery.delivery_app.utils.CustomPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {

    @Query("SELECT new com.delivery.delivery_app.dto.food.FoodResponse(f.id, f.name, f.image, f.price, f.oldPrice, f.likeCount, f.orderCount, r.id, r.name, r.address, r.latitude, r.longitude) FROM Food f JOIN f.foodCategory fc JOIN fc.restaurant r WHERE f.oldPrice <> f.price ORDER BY RANDOM()")
    Page<FoodResponse> getFlashSaleFoods(Pageable pageable);

}
