package ru.tkhapchaev.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByLogin(String login);

    boolean existsByLogin(String login);
}
