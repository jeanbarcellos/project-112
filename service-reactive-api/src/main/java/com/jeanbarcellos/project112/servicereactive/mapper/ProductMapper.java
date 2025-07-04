package com.jeanbarcellos.project112.servicereactive.mapper;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project112.servicereactive.dto.ProductRequest;
import com.jeanbarcellos.project112.servicereactive.dto.ProductResponse;
import com.jeanbarcellos.project112.servicereactive.model.Product;

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

    public Product copy(Product entity, ProductRequest request) {
        return entity.setName(request.getName())
                .setDescription(request.getDescription())
                .setPrice(request.getPrice())
                .setCategoryId(request.getCategoryId());
    }
}
