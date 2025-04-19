package com.jeanbarcellos.project112.serviceapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project112.serviceapi.dto.CategoryRequest;
import com.jeanbarcellos.project112.serviceapi.dto.CategoryResponse;
import com.jeanbarcellos.project112.serviceapi.service.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<CategoryResponse>>> getAll() {
        Flux<CategoryResponse> cagetories = service.findAll();
        return Mono.just(ResponseEntity.ok(cagetories));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CategoryResponse>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return service.create(request)
                .map(product -> ResponseEntity.status(201).body(product));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CategoryResponse>> update(@PathVariable String id,
            @RequestBody @Valid CategoryRequest request) {
        return service.update(id, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}