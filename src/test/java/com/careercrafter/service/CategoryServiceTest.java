package com.careercrafter.service;

import com.careercrafter.dto.request.CategoryReqDto;
import com.careercrafter.dto.response.CategoryRespDto;
import com.careercrafter.model.Category;
import com.careercrafter.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;

    @BeforeEach
    public void init(){
        category1 = new Category(1L, "IT", 1);
        category2 = new Category(2L, "Finance", 2);
    }

    @Test
    public void insertTest(){

        CategoryReqDto dto = new CategoryReqDto("IT", 1);

        categoryService.insert(dto);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("IT", captor.getValue().getName());
        Assertions.assertEquals(1, captor.getValue().getSequence());
    }

    @Test
    public void getAllTest(){

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<CategoryRespDto> result = categoryService.getAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("IT", result.get(0).name());

        verify(categoryRepository, times(1)).findAll();
    }
}