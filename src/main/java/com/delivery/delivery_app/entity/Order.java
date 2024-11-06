package com.delivery.delivery_app.entity;

import com.delivery.delivery_app.constant.OrderStatus;
import com.delivery.delivery_app.constant.OrderType;
import com.delivery.delivery_app.constant.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @ManyToOne()
    Driver driver;

    @ManyToOne()
    User user;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @CreationTimestamp
    Instant createdAt;

    Integer cost;

    Double distance;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    OrderType orderType;

    String originAddress;
    
    Double originLatitude;

    Double originLongitude;

    String destinationAddress;
    
    Double destinationLatitude;

    Double destinationLongitude;

//    Double destinationLatitude;
//
//    Double destinationLongitude;
//
//    Double destinationLatitude;
//
//    Double destinationLongitude;
//

}

