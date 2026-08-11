package com.careercrafter.repository;

import com.careercrafter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u from User u
            where u.username = ?1
            and u.isActivated = true
            """)
    Optional<User> loadUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            select u from User u
            order by u.id
            """)
    List<User> getAllUsers(Pageable pageable);

    @Query("""
            select u from User u
            where lower(u.username) like lower(concat('%', :keyword, '%'))
            order by u.id
            """)
    List<User> searchUsers(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}