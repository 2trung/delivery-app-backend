package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.FoodCustomizeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCustomizeOptionRepository extends JpaRepository<FoodCustomizeOption, String> {
}
