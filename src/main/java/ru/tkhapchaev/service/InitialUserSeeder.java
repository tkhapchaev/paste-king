package ru.tkhapchaev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.repository.AppUserRepository;

@Component
public class InitialUserSeeder implements ApplicationRunner {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final String login;
    private final String password;

    public InitialUserSeeder(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.initial-user.login}") String login,
            @Value("${app.initial-user.password}") String password
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.login = login;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appUserRepository.existsByLogin(login)) {
            appUserRepository.save(new AppUser(login, passwordEncoder.encode(password)));
        }
    }
}
