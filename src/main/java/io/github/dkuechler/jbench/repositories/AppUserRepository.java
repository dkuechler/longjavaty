package io.github.dkuechler.jbench.repositories;

import io.github.dkuechler.jbench.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    
    Optional<AppUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
}