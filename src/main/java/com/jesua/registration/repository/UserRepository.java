package com.jesua.registration.repository;

import com.jesua.registration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String name);
    Optional<User> findByEmailAndActiveTrue(String email);
    Boolean existsByEmail(String email);

    Optional<User> findByPasswordTokens_Token(String token);
}
