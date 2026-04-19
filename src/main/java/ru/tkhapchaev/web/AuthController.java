package ru.tkhapchaev.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.service.AuthService;
import ru.tkhapchaev.service.CurrentUserService;
import ru.tkhapchaev.web.dto.LoginRequest;
import ru.tkhapchaev.web.dto.LoginResponse;
import ru.tkhapchaev.web.dto.UserResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authorize with login and password and return JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Return current authorized user")
    public UserResponse me(Authentication authentication) {
        AppUser user = currentUserService.getCurrentUser(authentication);

        return UserResponse.from(user);
    }
}
