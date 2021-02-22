package com.jesua.registration.repository;

import com.jesua.registration.entity.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Integer> {

    Optional<PasswordToken> findByToken(String token);
}
