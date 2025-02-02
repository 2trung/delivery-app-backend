package com.delivery.delivery_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.delivery_app.entity.OrderLocation;

public interface OrderLocationRepository extends JpaRepository<OrderLocation, String> {
}
