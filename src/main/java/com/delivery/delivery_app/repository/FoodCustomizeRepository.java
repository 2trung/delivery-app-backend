package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.FoodCustomize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCustomizeRepository extends JpaRepository<FoodCustomize, String> {
}
