package com.healthtrack.repository;

import com.healthtrack.entity.User;
import com.healthtrack.entity.UserProviderLink;
import com.healthtrack.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProviderLinkRepository extends JpaRepository<UserProviderLink, Long> {

    List<UserProviderLink> findByUser(User user);

    Optional<UserProviderLink> findByUserAndProvider(User user, Provider provider);

    Optional<UserProviderLink> findByUserAndIsPrimaryTrue(User user);
}
