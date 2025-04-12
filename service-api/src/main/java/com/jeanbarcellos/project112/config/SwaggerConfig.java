package com.jeanbarcellos.project112.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "WebFlux CRUD API", version = "v1")
)
public class SwaggerConfig {
}