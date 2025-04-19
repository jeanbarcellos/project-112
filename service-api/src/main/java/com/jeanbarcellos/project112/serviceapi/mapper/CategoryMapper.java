package com.jeanbarcellos.project112.serviceapi.mapper;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project112.serviceapi.dto.CategoryRequest;
import com.jeanbarcellos.project112.serviceapi.dto.CategoryResponse;
import com.jeanbarcellos.project112.serviceapi.model.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return new Category(null, request.getName());
    }

    public CategoryResponse toResponse(Category entity) {
        return new CategoryResponse(entity.getId(), entity.getName());
    }

    public Category copy(Category entity, CategoryRequest request) {
        return entity.setName(request.getName());
    }
}