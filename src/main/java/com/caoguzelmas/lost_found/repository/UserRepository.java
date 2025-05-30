package com.caoguzelmas.lost_found.repository;

import com.caoguzelmas.lost_found.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserId(final Long userId);

    Optional<User> findByUsername(final String username);
}
