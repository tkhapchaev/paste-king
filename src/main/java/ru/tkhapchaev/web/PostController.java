package ru.tkhapchaev.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.service.CurrentUserService;
import ru.tkhapchaev.service.PostService;
import ru.tkhapchaev.web.dto.CreatePostRequest;
import ru.tkhapchaev.web.dto.PageResponse;
import ru.tkhapchaev.web.dto.PostResponse;
import ru.tkhapchaev.web.dto.UpdatePostRequest;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts")
public class PostController {
    private final CurrentUserService currentUserService;
    private final PostService postService;

    public PostController(CurrentUserService currentUserService, PostService postService) {
        this.currentUserService = currentUserService;
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create text post for current user")
    public PostResponse createPost(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request
    ) {
        AppUser user = currentUserService.getCurrentUser(authentication);

        return postService.createPost(user, request);
    }

    @GetMapping
    @Operation(summary = "Get current user's posts with pagination")
    public PageResponse<PostResponse> getMyPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        AppUser user = currentUserService.getCurrentUser(authentication);

        return postService.getMyPosts(user, page, limit);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update current user's post")
    public PostResponse updatePost(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        AppUser user = currentUserService.getCurrentUser(authentication);

        return postService.updatePost(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user's post")
    public void deletePost(Authentication authentication, @PathVariable Long id) {
        AppUser user = currentUserService.getCurrentUser(authentication);

        postService.deletePost(user, id);
    }
}
