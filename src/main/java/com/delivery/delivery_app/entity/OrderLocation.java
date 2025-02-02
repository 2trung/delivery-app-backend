package com.delivery.delivery_app.entity;

import com.delivery.delivery_app.constant.LocationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class OrderLocation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    String id;

    @Enumerated(EnumType.STRING)
    LocationType locationType;

    @ManyToOne()
    @JsonBackReference
    Order order;

    String addressLine1;

    String addressLine2;

    Double latitude;

    Double longitude;

    Integer sequence;
}

