package com.infobip.spring.data.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestConstructor;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(PER_CLASS)
@SpringBootTest(classes = Main.class)
public abstract class TestBase {

    public static final Instant BEGINNING_OF_2021 = LocalDate.of(2021, 1, 1)
                                                       .atStartOfDay(ZoneOffset.UTC).toInstant();

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @AfterEach
    public void clearRepositories() {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
