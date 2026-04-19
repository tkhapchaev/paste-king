package ru.tkhapchaev.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @NotBlank(message = "Text must not be blank")
        @Size(max = 65535, message = "Text must be at most 65535 characters")
        String text
) {
}
