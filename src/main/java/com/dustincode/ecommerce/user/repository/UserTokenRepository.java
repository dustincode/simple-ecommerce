package com.dustincode.ecommerce.user.repository;

import com.dustincode.ecommerce.user.entity.UserToken;
import com.dustincode.ecommerce.user.entity.enumerations.TokenChannel;
import com.dustincode.ecommerce.user.entity.enumerations.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByTokenAndTypeAndChannel(String token, TokenType type, TokenChannel channel);
}
