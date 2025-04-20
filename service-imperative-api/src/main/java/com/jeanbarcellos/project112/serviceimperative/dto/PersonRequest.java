package com.jeanbarcellos.project112.serviceimperative.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonRequest {

    @NotBlank
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;
}