package com.careercrafter.repository;

import com.careercrafter.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            select c
            from Category c
            where lower(c.name) like
            lower(concat('%', :keyword, '%'))
            """)
    List<Category> search(
            @Param("keyword") String keyword
    );
}