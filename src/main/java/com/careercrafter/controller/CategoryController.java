package com.careercrafter.controller;
import com.careercrafter.dto.request.CategoryReqDto;
import com.careercrafter.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/add")
    public void insert(@RequestBody CategoryReqDto dto){
        categoryService.insert(dto);
    }
}