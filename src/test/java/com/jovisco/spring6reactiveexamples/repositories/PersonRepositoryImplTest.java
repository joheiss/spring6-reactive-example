package com.jovisco.spring6reactiveexamples.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jovisco.spring6reactiveexamples.domain.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PersonRepositoryImplTest {

    PersonRepository personRepository = new PersonRepositoryImpl();
    
    @Test
    void testMonoGetByIdBlock() {
        // this is BAD because it is BLOCKING
        Mono<Person> personMono = personRepository.getById(3);
        var person = personMono.block();

        assertEquals(person.getId(), 3);

        System.out.println(person.toString());
        System.out.flush();
    }

    @Test
    void testMonoGetByIdSubscriber() {
        Mono<Person> personMono = personRepository.getById(2);

        assertTrue(personMono.hasElement().block());
        
        personMono.subscribe(p -> {
            System.out.println("subscribed: ");
            System.out.println(p.toString());
            System.out.flush();
        });
    }

    @Test
    void testMonoGetByIdFound() {
        Mono<Person> personMono = personRepository.getById(2);

        assertTrue(personMono.hasElement().block());
    }


    @Test
    void testMonoGetByIdFoundWithStepVerifier() {
        Mono<Person> personMono = personRepository.getById(2);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(System.out::println);
    }

    @Test
    void testMonoGetByIdNotFoundWithStepVerifier() {
        Mono<Person> personMono = personRepository.getById(9);

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void testMonoMapOperation() {
        Mono<Person> personMono = personRepository.getById(3);

        assertTrue(personMono.blockOptional().isPresent());

        personMono
            .map(Person::getFirstName)
            .map(String::toUpperCase)
            .subscribe(ucFirstName -> {
                System.out.println(ucFirstName);
                System.out.flush();
            });
    }

    @Test
    void testMonoNotFound() {
        Mono<Person> personMono = personRepository.getById(9);

        assertTrue(personMono.blockOptional().isEmpty());
    }

    @Test
    void testFluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        var person = personFlux.blockFirst();

        System.out.println(person.toString());
        System.out.flush();
    }

    @Test
    void testFluxSubscriber() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(person -> {
            System.out.println(person.toString());
            System.out.flush();
        });
    }

    @Test
    void testFluxMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux
            .filter(person -> !"Wurst".equals(person.getLastName()))
            .map(Person::getLastName)
            .map(String::toLowerCase)

            .subscribe(lastName -> {
                System.out.println(lastName);
                System.out.flush();
        });
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> listMono = personFlux.collectList();
        listMono.subscribe(list -> {
            list.forEach(System.out::println);
        });
    }

    @Test
    void testFluxFilterAndGetMono() {
        Flux<Person> personFlux = personRepository.findAll();

        var mono = personFlux
            .filter(p -> p.getId() == 3)
            .next(); // next emits the first object of the flux as a mono

        mono.subscribe(System.out::println);
    }

    @Test
    void testFluxFindByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 9;

        var mono = personFlux
            .filter(p -> p.getId() == id)
            .single() // single emits a single object or throws an exception
            .doOnError(System.out::println);
        mono
            .subscribe(
                person -> System.out.println(person),
                throwable -> System.out.println(throwable)
            );
    }
}
