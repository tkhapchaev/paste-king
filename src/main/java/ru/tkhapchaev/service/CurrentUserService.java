package ru.tkhapchaev.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.repository.AppUserRepository;
import ru.tkhapchaev.web.NotFoundException;

@Service
public class CurrentUserService {
    private final AppUserRepository appUserRepository;

    public CurrentUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser getCurrentUser(Authentication authentication) {
        return appUserRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Current user not found"));
    }
}
