package com.jeanbarcellos.project112.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project112.dto.ProductRequest;
import com.jeanbarcellos.project112.dto.ProductResponse;
import com.jeanbarcellos.project112.model.Product;
import com.jeanbarcellos.project112.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Flux<ProductResponse> findAll() {
        return repository.findAll()
                .map(product -> new ProductResponse(
                        product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                        product.getCategoryId()));
    }

    public Mono<ProductResponse> findById(String id) {
        return repository.findById(id)
                .map(product -> new ProductResponse(
                        product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                        product.getCategoryId()));
    }

    public Mono<ProductResponse> save(ProductRequest request) {
        Product product = new Product(null, request.getName(), request.getDescription(), request.getPrice(),
                request.getCategoryId());
        return repository.save(product)
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                        p.getCategoryId()));
    }

    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}