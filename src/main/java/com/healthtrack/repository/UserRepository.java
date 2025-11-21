package com.healthtrack.repository;

import com.healthtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByHealthId(String healthId);
    Optional<User> findByPhone(String phone);
}
