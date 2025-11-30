package io.github.dkuechler.longjavaty.users.service;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;

    public UserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public AppUser getOrCreateUser(UUID id, String email) {
        return appUserRepository.findById(id)
                .orElseGet(() -> {
                    AppUser newUser = new AppUser(email);
                    newUser.setId(id);
                    return appUserRepository.save(newUser);
                });
    }
}
