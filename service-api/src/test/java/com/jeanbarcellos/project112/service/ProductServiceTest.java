package com.jeanbarcellos.project112.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jeanbarcellos.project112.dto.ProductRequest;
import com.jeanbarcellos.project112.dto.ProductResponse;
import com.jeanbarcellos.project112.mapper.ProductMapper;
import com.jeanbarcellos.project112.model.Product;
import com.jeanbarcellos.project112.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Spy
    private ProductMapper mapper;

    @InjectMocks
    private ProductService service;

    @Test
    void findAll_noProducts_shouldReturnEmptyList() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findAll_withProducts_shouldReturnProductList() {
        // Arrange
        Product p1 = new Product("1", "P1", "Desc1", BigDecimal.TEN, "cat1");
        Product p2 = new Product("2", "P2", "Desc2", BigDecimal.ONE, "cat2");

        when(repository.findAll()).thenReturn(Flux.just(p1, p2));

        // Act & Assert
        StepVerifier.create(service.findAll())
                .expectNext(new ProductResponse("1", "P1", "Desc1", BigDecimal.TEN, "cat1"))
                .expectNext(new ProductResponse("2", "P2", "Desc2", BigDecimal.ONE, "cat2"))
                .verifyComplete();
    }

    @Test
    void findById_existingId_shouldReturnProduct() {
        // Arrange
        Product product = new Product("1", "P1", "Desc", BigDecimal.TEN, "cat1");

        when(repository.findById("1")).thenReturn(Mono.just(product));

        // Act & Assert
        StepVerifier.create(service.findById("1"))
                .expectNext(new ProductResponse("1", "P1", "Desc", BigDecimal.TEN, "cat1"))
                .verifyComplete();
    }

    @Test
    void findById_nonExistentId_shouldReturnEmpty() {
        // Arrange
        when(repository.findById("999")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.findById("999"))
                .verifyComplete();
    }

    @Test
    void create_validRequest_shouldReturnSavedProduct() {
        // Arrange
        ProductRequest request = new ProductRequest("P1", "Desc", BigDecimal.TEN, "cat1");
        Product product = new Product(null, "P1", "Desc", BigDecimal.TEN, "cat1");
        Product saved = new Product("123", "P1", "Desc", BigDecimal.TEN, "cat1");

        when(repository.save(product)).thenReturn(Mono.just(saved));

        // Act & Assert
        StepVerifier.create(service.create(request))
                .expectNext(new ProductResponse("123", "P1", "Desc", BigDecimal.TEN, "cat1"))
                .verifyComplete();
    }

    @Test
    void update_whenProductExists_shouldUpdateAndReturnResponse() {
        // Arrange
        String id = "123";
        Product existing = new Product(id, "old-name", "Desc", BigDecimal.TEN, "cat1");
        ProductRequest request = new ProductRequest("new-name", "new-desc", BigDecimal.TEN, "cat2");
        Product updated = new Product(id, "new-name", "new-desc", BigDecimal.TEN, "cat2");

        when(repository.findById(id)).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        // Act
        Mono<ProductResponse> result = service.update(id, request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(
                        response -> response.getId().equals(id) &&
                        response.getName().equals(updated.getName()))
                .verifyComplete();

        verify(repository).findById(id);
        verify(repository).save(existing);
    }

    @Test
    void delete_existingId_shouldComplete() {
        // Arrange
        when(repository.deleteById("123")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete("123"))
                .verifyComplete();
    }
}
