package io.github.dkuechler.longjavaty.users.repository;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    
    Optional<AppUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
}