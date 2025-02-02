package com.delivery.delivery_app.entity;

import com.delivery.delivery_app.constant.ProductCategory;
import com.delivery.delivery_app.constant.ProductSize;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class DeliveryOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    Integer cod = 0;

    Integer deliveryCost;

    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    String note;

    @Enumerated(EnumType.STRING)
    ProductSize productSize;

    String senderName;

    String senderPhone;

    String receiverName;

    String receiverPhone;

    @OneToOne()
    @JsonBackReference
    Order order;
}
