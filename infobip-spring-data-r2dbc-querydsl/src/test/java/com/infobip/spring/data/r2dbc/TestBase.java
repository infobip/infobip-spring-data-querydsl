package com.infobip.spring.data.r2dbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Mono;

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

    @AfterEach
    public void clearRepositories() {
        Mono.when(repositories.stream().map(ReactiveCrudRepository::deleteAll).collect(Collectors.toList()))
            .block(Duration.ofSeconds(10));
    }
}
