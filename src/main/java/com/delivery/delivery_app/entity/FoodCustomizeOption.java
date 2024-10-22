package com.delivery.delivery_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class FoodCustomizeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    String name;

    Integer price;

    Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "food_customize_id")
    FoodCustomize foodCustomize;
}
