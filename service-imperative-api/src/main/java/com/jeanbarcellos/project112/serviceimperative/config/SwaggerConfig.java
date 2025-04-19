package com.jeanbarcellos.project112.serviceimperative.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Project 112 - Imperative", version = "v1")
)
public class SwaggerConfig {
}