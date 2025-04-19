package com.jeanbarcellos.project112.servicereactive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project112.servicereactive.dto.ProductRequest;
import com.jeanbarcellos.project112.servicereactive.dto.ProductResponse;
import com.jeanbarcellos.project112.servicereactive.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<ProductResponse>>> getAll() {
        Flux<ProductResponse> products = service.findAll();
        return Mono.just(ResponseEntity.ok(products));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return service.create(request)
                .map(product -> ResponseEntity.status(201).body(product));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> update(@PathVariable String id,
            @RequestBody @Valid ProductRequest request) {
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