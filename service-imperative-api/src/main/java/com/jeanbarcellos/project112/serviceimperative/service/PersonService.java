package com.jeanbarcellos.project112.serviceimperative.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project112.serviceimperative.domain.Person;
import com.jeanbarcellos.project112.serviceimperative.dto.PersonRequest;
import com.jeanbarcellos.project112.serviceimperative.dto.PersonResponse;
import com.jeanbarcellos.project112.serviceimperative.mapper.PersonMapper;
import com.jeanbarcellos.project112.serviceimperative.repository.PersonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;
    private final PersonMapper mapper;

    public List<PersonResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<PersonResponse> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public PersonResponse save(PersonRequest request) {
        Person person = mapper.toEntity(request);
        Person saved = repository.save(person);
        return mapper.toResponse(saved);
    }

    public Optional<PersonResponse> update(Long id, PersonRequest request) {
        return repository.findById(id)
                .map(existing -> repository.save(mapper.copy(existing, request)))
                .map(mapper::toResponse);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}