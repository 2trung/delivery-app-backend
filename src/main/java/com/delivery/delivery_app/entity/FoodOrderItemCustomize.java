package com.delivery.delivery_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class FoodOrderItemCustomize {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @ManyToOne()
    @JsonBackReference
    FoodOrderItem foodOrderItem;

    @ManyToOne()
    FoodCustomize foodCustomize;

    @OneToMany(mappedBy = "foodOrderItemCustomize", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<FoodOrderItemCustomizeOption> foodOrderItemCustomizeOptions;
}
