package com.delivery.delivery_app.entity;

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
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    Integer price;

    Integer oldPrice;

    String image;

    String name;

    Integer orderCount;

    Integer likeCount;

    @ManyToOne()
    FoodCategory foodCategory;

    @OneToMany()
    @JsonManagedReference
    List<FoodCustomize> foodCustomizes;
}
