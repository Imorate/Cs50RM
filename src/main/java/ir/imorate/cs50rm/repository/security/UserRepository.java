package ir.imorate.cs50rm.repository.security;

import ir.imorate.cs50rm.entity.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(@NonNull String username);

    Optional<User> findByEmailIgnoreCase(@NonNull String email);

}