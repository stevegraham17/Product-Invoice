package com.products.products.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.products.products.models.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // Fetch the latest token for a user
    @Query("SELECT p FROM PasswordResetToken p WHERE p.user.id = :userId ORDER BY p.expiryDate DESC")
    Optional<PasswordResetToken> findFirstByUserIdOrderByExpiryDateDesc(@Param("userId") Long userId);

    Optional<PasswordResetToken> findByUserId(Long userId); // To check if the token already exists
}
