package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository  extends JpaRepository<Food, String> {
}
