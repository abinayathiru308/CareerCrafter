package com.careercrafter.controller;

import com.careercrafter.dto.request.CategoryReqDto;
import com.careercrafter.dto.response.CategoryRespDto;
import com.careercrafter.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/add")
    public void insert(
            @RequestBody CategoryReqDto dto
    ) {
        categoryService.insert(dto);
    }

    @GetMapping("/get-all")
    public List<CategoryRespDto> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/search")
    public List<CategoryRespDto> search(
            @RequestParam String keyword
    ) {
        return categoryService.search(keyword);
    }

    @PutMapping("/update/{id}")
    public void update(
            @PathVariable long id,
            @RequestBody CategoryReqDto dto
    ) {
        categoryService.update(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(
            @PathVariable long id
    ) {
        categoryService.delete(id);
    }
}