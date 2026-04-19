package ru.tkhapchaev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.domain.Post;
import ru.tkhapchaev.repository.PostRepository;
import ru.tkhapchaev.web.NotFoundException;
import ru.tkhapchaev.web.dto.CreatePostRequest;
import ru.tkhapchaev.web.dto.PageResponse;
import ru.tkhapchaev.web.dto.PostResponse;
import ru.tkhapchaev.web.dto.UpdatePostRequest;

@Service
public class PostService {
    private static final int MAX_PAGE_LIMIT = 100;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse createPost(AppUser author, CreatePostRequest request) {
        Post savedPost = postRepository.save(new Post(author, request.text()));

        return PostResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getMyPosts(AppUser author, int page, int limit) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), normalizeLimit(limit));

        Page<PostResponse> posts = postRepository.findAllByAuthorOrderByCreatedAtDescIdDesc(author, pageable)
                .map(PostResponse::from);

        return PageResponse.from(posts);
    }

    @Transactional
    public PostResponse updatePost(AppUser author, Long id, UpdatePostRequest request) {
        Post post = postRepository.findByIdAndAuthor(id, author)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        post.updateText(request.text());

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(AppUser author, Long id) {
        Post post = postRepository.findByIdAndAuthor(id, author)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        postRepository.delete(post);
    }

    private int normalizeLimit(int limit) {
        if (limit < 1) {
            return 20;
        }

        return Math.min(limit, MAX_PAGE_LIMIT);
    }
}
