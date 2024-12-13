package com.delivery.delivery_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class FoodCustomize {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    String name;

    Integer minimumChoices;

    Integer maximumChoices;

    @ManyToOne()
    @JsonBackReference
    Food food;

    @OneToMany(mappedBy = "foodCustomize", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<FoodCustomizeOption> foodCustomizeOptions;
}
