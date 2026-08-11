package com.careercrafter.mapper;
import com.careercrafter.dto.response.CategoryRespDto;
import com.careercrafter.model.Category;

public class CategoryMapper {

    public static CategoryRespDto convertEntityToDto(Category category) {

        return new CategoryRespDto(

                category.getId(),
                category.getName(),
                category.getSequence()

        );
    }

}