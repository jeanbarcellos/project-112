package com.jeanbarcellos.project112.servicereactive.config;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@DataMongoTest
public class TestcontainersConfig {

    public static final String MONGO_CONFIG_URI = "spring.data.mongodb.uri";
    public static final String MONGO_IMAGE_VERSION = "mongo:5.0";

    static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE_VERSION));
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Bean
    public MongoDBContainer mongoContainer() {
        return mongoDBContainer;
    }
}