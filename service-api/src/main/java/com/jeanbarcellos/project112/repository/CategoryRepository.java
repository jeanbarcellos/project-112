package com.jeanbarcellos.project112.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.jeanbarcellos.project112.model.Category;

@Repository
public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}