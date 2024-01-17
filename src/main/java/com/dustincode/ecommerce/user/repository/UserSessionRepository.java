package com.dustincode.ecommerce.user.repository;

import com.dustincode.ecommerce.user.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByAccessTokenIdAndRefreshTokenId(String accessTokenId, String refreshTokenId);
    boolean existsByAccessTokenId(String accessTokenId);
    void deleteByAccessTokenId(String accessTokenId);
}
