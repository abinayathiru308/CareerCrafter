package com.careercrafter.repository;
import com.careercrafter.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Long> {
}