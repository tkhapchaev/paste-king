package ru.tkhapchaev.web.dto;

import java.time.Instant;
import ru.tkhapchaev.domain.Post;

public record PostResponse(
        Long id,
        String text,
        boolean isEdited,
        Instant createdAt,
        Instant updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getText(), post.isEdited(), post.getCreatedAt(), post.getUpdatedAt());
    }
}
