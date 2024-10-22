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
public class FoodCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    String name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    Restaurant restaurant;

    @OneToMany(mappedBy = "foodCategory", fetch = FetchType.LAZY)
    List<Food> foods;

}
