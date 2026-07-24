package com.careercrafter.repository;
import com.careercrafter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username = ?1 AND u.isActivated = true")
    Optional<User> loadUserByUsername(String username);
}