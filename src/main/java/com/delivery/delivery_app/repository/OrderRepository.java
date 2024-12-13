package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.dto.order.OrderResponse;
import com.delivery.delivery_app.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
//    @Query("SELECT o FROM Order o JOIN FETCH o.locations WHERE o.id = :orderId")
//    @EntityGraph(attributePaths = {"locations"})
//    Optional<Order> findById(String orderId);
    Page<Order> findAllByUserId(String userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findAllOrdersByUserId(String userId, Pageable pageable);
}
