package com.jeanbarcellos.project112.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.jeanbarcellos.project112.model.Product;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}