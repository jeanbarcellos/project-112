package com.jeanbarcellos.project112.servicereactive.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    private String name;
}