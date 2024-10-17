package com.delivery.delivery_app.repository;

import com.delivery.delivery_app.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findByPhoneNumber(String phoneNumber);
}
