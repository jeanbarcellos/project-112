package com.jeanbarcellos.project112.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project112.dto.CategoryRequest;
import com.jeanbarcellos.project112.dto.CategoryResponse;
import com.jeanbarcellos.project112.model.Category;
import com.jeanbarcellos.project112.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public Flux<CategoryResponse> findAll() {
        return repository.findAll().map(category -> new CategoryResponse(category.getId(), category.getName()));
    }

    public Mono<CategoryResponse> findById(String id) {
        return repository.findById(id).map(c -> new CategoryResponse(c.getId(), c.getName()));
    }

    public Mono<CategoryResponse> save(CategoryRequest dto) {
        Category category = new Category(null, dto.getName());
        return repository.save(category)
                .map(c -> new CategoryResponse(c.getId(), c.getName()));
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}