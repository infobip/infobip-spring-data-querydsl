package com.infobip.spring.data.r2dbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(PER_CLASS)
@SpringBootTest(classes = Main.class)
public abstract class TestBase {

    @Autowired
    private List<ReactiveCrudRepository<?, ?>> repositories;

    @Autowired
    private ReactiveTransactionManager reactiveTransactionManager;

    @AfterEach
    public void clearRepositories() {
        TransactionalOperator transactionalOperator = TransactionalOperator.create(reactiveTransactionManager);
        block(Flux.concat(repositories.stream()
                                      .map(reactiveCrudRepository -> transactionalOperator.execute(
                                              status -> reactiveCrudRepository.deleteAll()
                                      ).then())
                                      .collect(Collectors.toList()))
                  .collectList());
    }

    @Nullable
    <T> T block(Mono<T> mono) {
        return mono.block(Duration.ofSeconds(10));
    }

    @Nullable
    <T> List<T> block(Flux<T> flux) {
        return block(flux.collectList());
    }
}
