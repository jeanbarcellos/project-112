package com.jeanbarcellos.project112.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project112.dto.CategoryRequest;
import com.jeanbarcellos.project112.dto.CategoryResponse;
import com.jeanbarcellos.project112.mapper.CategoryMapper;
import com.jeanbarcellos.project112.model.Category;
import com.jeanbarcellos.project112.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public Flux<CategoryResponse> findAll() {
        return repository.findAll()
                .map(mapper::toResponse);
    }

    public Mono<CategoryResponse> findById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Mono<CategoryResponse> create(CategoryRequest request) {
        Category category = mapper.toEntity(request);
        return repository.save(category)
                .map(mapper::toResponse);
    }

    public Mono<CategoryResponse> update(String id, CategoryRequest request) {
        return repository.findById(id)
                .flatMap(entity -> repository.save(mapper.copy(entity, request)))
                .map(mapper::toResponse);
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}