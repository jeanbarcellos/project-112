package com.jeanbarcellos.project112.serviceimperative.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanbarcellos.project112.serviceimperative.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}