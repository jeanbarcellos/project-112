package com.jeanbarcellos.project112.servicereactive.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project112.servicereactive.dto.ProductRequest;
import com.jeanbarcellos.project112.servicereactive.dto.ProductResponse;
import com.jeanbarcellos.project112.servicereactive.mapper.ProductMapper;
import com.jeanbarcellos.project112.servicereactive.model.Product;
import com.jeanbarcellos.project112.servicereactive.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Flux<ProductResponse> findAll() {
        return repository.findAll()
                .map(mapper::toResponse);
    }

    public Mono<ProductResponse> findById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Mono<ProductResponse> create(ProductRequest request) {
        Product product = mapper.toEntity(request);

        return repository.save(product)
                .map(mapper::toResponse);
    }

    public Mono<ProductResponse> update(String id, ProductRequest request) {
        return repository.findById(id)
                .flatMap(entity -> repository.save(mapper.copy(entity, request)))
                .map(mapper::toResponse);
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}