package com.delivery.delivery_app.entity;

import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @ManyToOne()
    @JsonManagedReference
    Driver driver;

    @ManyToOne()
    User user;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Integer cost;

    Double distance;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    OrderType orderType;

    Integer locationSequence;

    String paymentIntentId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderLocation> locations;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<FoodOrderItem> foodOrderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    DeliveryOrderDetail deliveryOrderDetail;
}

