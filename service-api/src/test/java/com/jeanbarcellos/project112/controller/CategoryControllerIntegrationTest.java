package com.jeanbarcellos.project112.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jeanbarcellos.project112.config.TestcontainersConfig;
import com.jeanbarcellos.project112.dto.CategoryRequest;
import com.jeanbarcellos.project112.dto.CategoryResponse;
import com.jeanbarcellos.project112.model.Category;
import com.jeanbarcellos.project112.repository.CategoryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(TestcontainersConfig.MONGO_IMAGE_VERSION);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add(TestcontainersConfig.MONGO_CONFIG_URI, mongoDBContainer::getReplicaSetUrl);
    }

    private final String BASE_URI = "/api/categories";
    private final CategoryRequest validRequest = new CategoryRequest("cat-c");
    private CategoryResponse responseSaved;

    @BeforeAll
    void setup() {
        // Clean and Seed database
        categoryRepository.deleteAll()
                .thenMany(categoryRepository.saveAll(List.of(
                        new Category(null, "cat-a"),
                        new Category(null, "cat-b"))))
                .blockLast();
    }

    @Test
    @Order(1)
    @DisplayName("GET /categories deve retornar todas as categorias")
    void getAll_shouldReturnAllCategories() {
        // Act
        List<CategoryResponse> responses = webTestClient.get()
                .uri(BASE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(CategoryResponse::getName)
                .containsExactlyInAnyOrder("cat-a", "cat-b");
    }

    // Insert ---------------------------------

    @Test
    @Order(2)
    @SuppressWarnings("null")
    @DisplayName("POST /categories deve criar uma nova categoria")
    void create_shouldPersistCategoryAndReturnResponse() {
        // Act
        CategoryResponse response = webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CategoryResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotBlank();
        assertThat(response.getName()).isEqualTo(validRequest.getName());

        // Save para testes futuros
        this.responseSaved = response;
    }

    @Test
    @Order(3)
    @DisplayName("POST /categories com nome vazio deve retornar erro de validação")
    void create_whenNameIsBlank_shouldReturnValidationError() {
        // Arrange
        CategoryRequest invalidRequest = new CategoryRequest("");

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // getById ------------------------------

    @Test
    @Order(4)
    @SuppressWarnings("null")
    @DisplayName("GET /categories/{id} deve retornar categoria existente")
    void getById_whenExists_shouldReturnCategory() {
        // Arrange
        // Category category = categoryRepository.save(new Category(null, "cat-get")).block();

        // Act
        CategoryResponse response = webTestClient.get()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(responseSaved.getId());
        assertThat(response.getName()).isEqualTo(responseSaved.getName());
    }

    @Test
    @Order(5)
    @DisplayName("GET /categories/{id} deve retornar 404 se a categoria não existir")
    void getById_whenNotExists_shouldReturnNotFound() {
        // Act & Assert
        webTestClient.get()
                .uri(BASE_URI + "/{id}", "non-existent-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    // update ------------------------------

    @Test
    @Order(6)
    @SuppressWarnings("null")
    @DisplayName("update - Deve atualizar a categoria existente")
    void update_shouldUpdateCategory_whenExists() {
        // Arrange
        // String categoryId = categoryRepository.findAll().blockFirst().getId();
        CategoryRequest updateRequest = new CategoryRequest("cat-updated");

        // Act
        CategoryResponse response = webTestClient.put()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(responseSaved.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    @Order(7)
    @DisplayName("update - Deve retornar 404 se a categoria não existir")
    void update_shouldReturnNotFound_whenCategoryDoesNotExist() {
        // Arrange
        String nonExistentId = "non-existent-id";
        CategoryRequest updateRequest = new CategoryRequest("cat-x");

        // Act & Assert
        webTestClient.put()
                .uri(BASE_URI + "/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    // delete ------------------------------

    @Test
    @Order(8)
    @DisplayName("DELETE /categories/{id} deve remover categoria existente")
    void delete_shouldRemoveCategory() {
        // Arrange
        // Category category = categoryRepository.save(new Category(null, "cat-delete")).block();

        // Arrange && Act
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .exchange()
                .expectStatus().isNoContent();

        // Assert
        boolean exists = categoryRepository.findById(responseSaved.getId()).hasElement().block();
        assertThat(exists).isFalse();
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /categories/{id} com id inexistente deve retornar 204 mesmo assim")
    void delete_whenIdNotExists_shouldReturnNoContent() {
        // Act & Assert
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", "non-existent-id")
                .exchange()
                .expectStatus().isNoContent();
    }
}
