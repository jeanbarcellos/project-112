package com.jeanbarcellos.project112.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jeanbarcellos.project112.dto.CategoryRequest;
import com.jeanbarcellos.project112.dto.CategoryResponse;
import com.jeanbarcellos.project112.mapper.CategoryMapper;
import com.jeanbarcellos.project112.model.Category;
import com.jeanbarcellos.project112.repository.CategoryRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @Spy
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryService service;

    @Test
    void findAll_noCategories_shouldReturnEmptyList() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.findAll())
                .expectNextCount(0)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void findAll_withCategories_shouldReturnCategoryList() {
        // Arrange
        Category c1 = new Category("1", "Cat 1");
        Category c2 = new Category("2", "Cat 2");

        when(repository.findAll()).thenReturn(Flux.just(c1, c2));

        // Act & Assert
        StepVerifier.create(service.findAll())
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void findById_existingId_shouldReturnCategory() {
        // Arrange
        String id = "1";
        Category category = new Category(id, "Cat 1");

        when(repository.findById(id)).thenReturn(Mono.just(category));

        // Act & Assert
        StepVerifier.create(service.findById(id))
                .expectNextMatches(c -> c.getId().equals(id) && c.getName().equals("Cat 1"))
                .verifyComplete();

        verify(repository).findById(id);
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        // Arrange
        String id = "non-existent";

        when(repository.findById(id)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.findById(id))
                .verifyComplete();

        verify(repository).findById(id);
    }

    @Test
    void create_validCategoryRequest_shouldReturnSavedCategory() {
        // Arrange
        CategoryRequest request = new CategoryRequest("New Category");
        Category savedCategory = new Category("123", "New Category");

        when(repository.save(any(Category.class))).thenReturn(Mono.just(savedCategory));

        // Act & Assert
        StepVerifier.create(service.create(request))
                .expectNextMatches(resp -> resp.getId().equals("123") && resp.getName().equals("New Category"))
                .verifyComplete();

        verify(repository).save(any(Category.class));
    }

    @Test
    @DisplayName("update - Deve atualizar categoria existente")
    void update_whenCategoryExists_shouldUpdateAndReturnResponse() {
        // Arrange
        String id = "123";
        Category existing = new Category(id, "old-name");
        CategoryRequest request = new CategoryRequest("new-name");
        Category updated = new Category(id, "new-name");

        when(repository.findById(id)).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        // Act
        Mono<CategoryResponse> result = service.update(id, request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals(id) &&
                        response.getName().equals("new-name"))
                .verifyComplete();

        verify(repository).findById(id);
        verify(repository).save(existing);
    }

    @Test
    void delete_existingId_shouldDeleteCategory() {
        // Arrange
        String id = "123";

        when(repository.deleteById(id)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete(id))
                .verifyComplete();

        verify(repository).deleteById(id);
    }
}
