package ru.tkhapchaev.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.repository.AppUserRepository;
import ru.tkhapchaev.web.dto.LoginRequest;
import ru.tkhapchaev.web.dto.LoginResponse;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByLogin(request.login())
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }

        return jwtService.createToken(user);
    }
}
