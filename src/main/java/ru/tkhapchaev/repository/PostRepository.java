package ru.tkhapchaev.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.domain.AppUser;
import ru.tkhapchaev.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByAuthorOrderByCreatedAtDescIdDesc(AppUser author, Pageable pageable);

    Optional<Post> findByIdAndAuthor(Long id, AppUser author);
}
