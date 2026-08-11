package com.careercrafter.service;

import com.careercrafter.dto.request.CategoryReqDto;
import com.careercrafter.dto.response.CategoryRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.CategoryMapper;
import com.careercrafter.model.Category;
import com.careercrafter.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void insert(CategoryReqDto dto) {

        if (categoryRepository.existsByNameIgnoreCase(dto.name())) {
            throw new IllegalArgumentException("Category already exists");
        }

        Category category = new Category();

        category.setName(dto.name());
        category.setSequence(dto.sequence());

        categoryRepository.save(category);
    }

    public List<CategoryRespDto> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::convertEntityToDto)
                .toList();
    }

    public List<CategoryRespDto> search(String keyword) {

        return categoryRepository.search(keyword)
                .stream()
                .map(CategoryMapper::convertEntityToDto)
                .toList();
    }

    public void update(long id, CategoryReqDto dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category id invalid"));

        category.setName(dto.name());
        category.setSequence(dto.sequence());

        categoryRepository.save(category);
    }

    public void delete(long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category id invalid"));

        categoryRepository.delete(category);
    }
}