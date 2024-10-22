package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, String> {
}
