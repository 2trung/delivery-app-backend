package com.delivery.delivery_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    String name;

    String address;

    String displayAddress;

    String image;

    Double latitude;

    Double longitude;

    String phoneNumber;

    Integer reviewCount;

    Float rating;

    Float deliveryRadius;

    @ManyToOne
    MerchantCategory merchantCategory;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    List<FoodCategory> foodCategories;
}
