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
public class FoodOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @ManyToOne()
    @JsonBackReference
    Order order;

    @ManyToOne()
    Food food;

    Integer quantity;

    Integer total;

    String note;

    @OneToMany(mappedBy = "foodOrderItem", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<FoodOrderItemCustomize> foodOrderItemCustomizes;
}
