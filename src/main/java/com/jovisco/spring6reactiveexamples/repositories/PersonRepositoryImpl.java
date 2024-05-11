package com.jovisco.spring6reactiveexamples.repositories;

import com.jovisco.spring6reactiveexamples.domain.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {

    Person hansi = Person.builder()
        .id(1)
        .firstName("Hansi")
        .lastName("Hampelmann")
        .build();

    Person willi = Person.builder()
        .id(2)
        .firstName("Willi")
        .lastName("Wurst")
        .build();

    Person gustl = Person.builder()
        .id(3)
        .firstName("Gustl")
        .lastName("Grün")
        .build();

    Flux<Person> persons = Flux.just(hansi, willi, gustl);

    @Override
    public Mono<Person> getById(final Integer id) {
        return persons.filter(p -> p.getId() == id).next();
    }

    @Override
    public Flux<Person> findAll() {
        return persons;
    }

}
