package io.github.dkuechler.longjavaty.users.repository;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppUserRepository userRepository;

    @Test
    void findByEmail_ShouldWork() {
        // Given
        AppUser user = new AppUser("test@example.com");
        entityManager.persistAndFlush(user);

        // When
        Optional<AppUser> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void existsByEmail_ShouldWork() {
        // Given
        AppUser user = new AppUser("test@example.com");
        entityManager.persistAndFlush(user);

        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }
}