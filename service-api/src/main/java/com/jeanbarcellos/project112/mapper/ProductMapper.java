package com.jeanbarcellos.project112.mapper;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project112.dto.ProductRequest;
import com.jeanbarcellos.project112.dto.ProductResponse;
import com.jeanbarcellos.project112.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return new Product(null,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategoryId());
    }

    public ProductResponse toResponse(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategoryId());
    }
}