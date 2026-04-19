package ru.tkhapchaev.web.dto;

import ru.tkhapchaev.domain.AppUser;

public record UserResponse(
        String login
) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getLogin());
    }
}
