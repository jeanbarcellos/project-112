package com.jeanbarcellos.project112.serviceimperative.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project112.serviceimperative.domain.Person;
import com.jeanbarcellos.project112.serviceimperative.dto.PersonRequest;
import com.jeanbarcellos.project112.serviceimperative.dto.PersonResponse;

@Component
public class PersonMapper {

    public Person toEntity(PersonRequest request) {
        return Person.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .build();
    }

    public PersonResponse toResponse(Person person) {
        return PersonResponse.builder()
                .id(person.getId())
                .name(person.getName())
                .birthDate(person.getBirthDate())
                .build();
    }

    public List<PersonResponse> toResponseList(List<Person> people) {
        return people.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Person copy(Person destination, PersonRequest source) {
        return destination
                .setName(source.getName())
                .setBirthDate(source.getBirthDate());
    }
}
