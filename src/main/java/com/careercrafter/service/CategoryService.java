package com.careercrafter.service;
import com.careercrafter.dto.request.CategoryReqDto;
import com.careercrafter.model.Category;
import com.careercrafter.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public void insert(CategoryReqDto dto) {
        Category category = new Category();
        category.setName(dto.name());
        category.setSequence(dto.sequence());
        categoryRepository.save(category);
    }
}