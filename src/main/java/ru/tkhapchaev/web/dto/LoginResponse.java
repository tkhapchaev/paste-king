package ru.tkhapchaev.web.dto;

import java.time.Instant;

public record LoginResponse(
        String login,
        String token,
        Instant expiresAt
) {
}
