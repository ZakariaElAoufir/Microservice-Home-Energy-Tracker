package com.leetjourney.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leetjourney.user_service.entity.User;
import java.util.List;
import java.util.Optional;


@Repository 
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
}
