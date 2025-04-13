package com.jeanbarcellos.project112.mapper;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project112.dto.CategoryRequest;
import com.jeanbarcellos.project112.dto.CategoryResponse;
import com.jeanbarcellos.project112.model.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return new Category(null, request.getName());
    }

    public CategoryResponse toResponse(Category entity) {
        return new CategoryResponse(entity.getId(), entity.getName());
    }
}