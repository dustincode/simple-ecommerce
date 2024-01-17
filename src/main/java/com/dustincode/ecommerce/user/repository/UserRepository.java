package com.dustincode.ecommerce.user.repository;

import com.dustincode.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    default User safeSave(User entity) {
        return save(entity);
    }

    boolean existsByEmailOrPhone(String email, String phone);
    boolean existsByPhoneAndIdIsNot(String phone, Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
}
