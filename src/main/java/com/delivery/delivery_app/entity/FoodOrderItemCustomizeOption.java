package com.delivery.delivery_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class FoodOrderItemCustomizeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @ManyToOne()
    @JsonBackReference
    FoodOrderItemCustomize foodOrderItemCustomize;

    @ManyToOne()
    FoodCustomizeOption foodCustomizeOption;
}
