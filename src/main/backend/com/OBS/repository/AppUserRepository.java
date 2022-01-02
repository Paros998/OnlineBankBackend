package com.OBS.repository;

import com.OBS.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional()
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);

    AppUser getByEmail(String email);

    AppUser getByUsername(String username);

    AppUser findByClient_clientId(Long clientId);
}
