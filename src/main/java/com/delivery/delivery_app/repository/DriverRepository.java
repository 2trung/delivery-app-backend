package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    @Query(value = "SELECT * FROM driver WHERE latitude >= :minLat AND latitude <= :maxLat AND longitude >= :minLon AND longitude <= :maxLon AND status = 'ONLINE'", nativeQuery = true)
    List<Driver> findNearbyDrivers(Double minLat, Double maxLat, Double minLon, Double maxLon);
    Optional<Driver> findByUserId(String id);
}
