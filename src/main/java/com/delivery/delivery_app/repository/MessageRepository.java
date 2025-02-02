package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository  extends JpaRepository<Message, String> {
//    @Query("SELECT m FROM Message m WHERE m.order.id = :orderId")
//    List<Message> findByOrderId(@Param("orderId") String orderId);

    List<Message> findByOrderId(String orderId);
}
