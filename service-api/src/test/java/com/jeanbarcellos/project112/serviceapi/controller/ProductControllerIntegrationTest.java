package com.jeanbarcellos.project112.serviceapi.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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

import com.jeanbarcellos.project112.serviceapi.config.TestcontainersConfig;
import com.jeanbarcellos.project112.serviceapi.dto.ProductRequest;
import com.jeanbarcellos.project112.serviceapi.dto.ProductResponse;
import com.jeanbarcellos.project112.serviceapi.model.Product;
import com.jeanbarcellos.project112.serviceapi.repository.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(TestcontainersConfig.MONGO_IMAGE_VERSION);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add(TestcontainersConfig.MONGO_CONFIG_URI, mongoDBContainer::getReplicaSetUrl);
    }

    private final String BASE_URI = "/api/products";

    private final ProductRequest validRequest = new ProductRequest(
            "Product C",
            "Descrição do Produto C",
            BigDecimal.valueOf(30.0),
            "categoria-1"
    );

    private ProductResponse responseSaved;

    @BeforeAll
    void setup() {
        productRepository.deleteAll()
                .thenMany(productRepository.saveAll(List.of(
                        new Product(null, "Product A", "Desc A", BigDecimal.valueOf(10.0), "categoria-1"),
                        new Product(null, "Product B", "Desc B", BigDecimal.valueOf(20.0), "categoria-2"))))
                .blockLast();
    }

    @Test
    @Order(1)
    @DisplayName("getAll - Deve retornar todos os produtos")
    void getAll_shouldReturnAllProducts() {
        // Act
        List<ProductResponse> responses = webTestClient.get()
                .uri(BASE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ProductResponse::getName)
                .containsExactlyInAnyOrder("Product A", "Product B");
    }

    @Test
    @Order(2)
    @SuppressWarnings("null")
    @DisplayName("create - Deve criar um novo produto")
    void create_shouldCreateNewProduct() {
        // Act
        ProductResponse response = webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotBlank();
        assertThat(response.getName()).isEqualTo(validRequest.getName());
        assertThat(response.getDescription()).isEqualTo(validRequest.getDescription());
        assertThat(response.getPrice()).isEqualTo(validRequest.getPrice());
        assertThat(response.getCategoryId()).isEqualTo(validRequest.getCategoryId());

        this.responseSaved = response;
    }

    @Test
    @Order(3)
    @DisplayName("create - Deve falhar ao criar produto com campos inválidos")
    void create_shouldFailWithInvalidRequest() {
        // Arrange
        ProductRequest invalidRequest = new ProductRequest("", "", null, "");

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    @SuppressWarnings("null")
    @DisplayName("getById - Deve retornar produto existente")
    void getById_shouldReturnProduct_whenExists() {
        // Act
        ProductResponse response = webTestClient.get()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(responseSaved.getId());
    }

    @Test
    @Order(5)
    @DisplayName("getById - Deve retornar 404 quando produto não existir")
    void getById_shouldReturnNotFound_whenNotExists() {
        webTestClient.get()
                .uri(BASE_URI + "/{id}", "invalid-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    @SuppressWarnings("null")
    @DisplayName("update - Deve atualizar produto existente")
    void update_shouldUpdateProduct_whenExists() {
        // Arrange
        ProductRequest updateRequest = new ProductRequest(
                "Produto Atualizado",
                "Descrição Atualizada",
                BigDecimal.valueOf(99.99),
                "categoria-2"
        );

        // Act
        ProductResponse response = webTestClient.put()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(responseSaved.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
        assertThat(response.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(response.getPrice()).isEqualTo(updateRequest.getPrice());
        assertThat(response.getCategoryId()).isEqualTo(updateRequest.getCategoryId());
    }

    @Test
    @Order(7)
    @DisplayName("update - Deve retornar 404 ao tentar atualizar produto inexistente")
    void update_shouldReturnNotFound_whenNotExists() {
        // Arrange
        ProductRequest request = new ProductRequest("X", "Y", BigDecimal.ONE, "categoria");

        // Act & Assert
        webTestClient.put()
                .uri(BASE_URI + "/{id}", "non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(8)
    @DisplayName("delete - Deve remover produto existente")
    void delete_shouldRemoveProduct() {
        // Act
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", responseSaved.getId())
                .exchange()
                .expectStatus().isNoContent();

        // Assert
        boolean exists = productRepository.findById(responseSaved.getId()).hasElement().block();
        assertThat(exists).isFalse();
    }

    @Test
    @Order(9)
    @DisplayName("delete - Deve retornar 204 mesmo se o produto não existir")
    void delete_shouldReturnNoContent_whenNotExists() {
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", "non-existent-id")
                .exchange()
                .expectStatus().isNoContent();
    }
}