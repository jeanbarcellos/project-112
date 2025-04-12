package com.jeanbarcellos.project112.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reactror")
public class ReactorController {

    @GetMapping("/test-01")
    public ResponseEntity<String> hello() {
        // var monoJust = Mono.just(System.currentTimeMillis());
        // var monoEmpy = Mono.empty();
        // var monoJustOrEmpty = Mono.justOrEmpty(Optional.empty());
        // var monoDefer = Mono.defer(Mono.just(System.currentTimeMillis()));

        Mono<String> mono = Mono.just("Jean Barcellos");

        mono.subscribe(
                value -> System.out.println("Recebido: " + value), // onNext
                error -> System.err.println("Erro: " + error), // onError
                () -> System.out.println("Concluído!") // onComplete
        );

        return ResponseEntity.ok("Test");
    }

    @GetMapping("/test-02")
    public ResponseEntity<String> test002() {
        Flux<String> flux = Flux.just("Jean", "Carlos", "Barcellos");

        flux.subscribe(
                value -> System.out.println("Recebido: " + value),
                error -> System.err.println("Erro: " + error),
                () -> System.out.println("Concluído!"));

        return ResponseEntity.ok("Test");
    }

    @GetMapping("/test-03")
    public ResponseEntity<String> test003() {
        Mono<String> mono = Mono.just("Jean")
                .map(nome -> nome.toUpperCase())
                .doOnNext(nome -> System.out.println("Nome transformado: " + nome));

        mono.subscribe();

        Flux<Integer> flux = Flux.just(1, 2, 3)
                .flatMap(num -> Mono.just(num * 10));

        flux.subscribe(System.out::println);

        return ResponseEntity.ok("Test");
    }

    @GetMapping("/test-04")
    public ResponseEntity<String> test004() {
        Mono<Object> mono = Mono.error(new RuntimeException("Falha ao carregar dados"))
                .onErrorReturn("Valor Padrão");

        mono.subscribe(System.out::println);

        //

        Mono<Object> mono2 = Mono.error(new RuntimeException("Erro ao buscar nome"))
                .onErrorResume(ex -> {
                    System.err.println("Tratando erro: " + ex.getMessage());
                    return Mono.just("Nome Padrão");
                });

        mono2.subscribe(System.out::println);

        return ResponseEntity.ok("Test");
    }
}
