package com.jeanbarcellos.project112.serviceimperative.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PersonResponse {

    private Long id;
    private String name;
    private LocalDate birthDate;
}