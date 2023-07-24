package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByUserId(UUID userId);

    Optional<EmailVerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken rt WHERE rt.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
