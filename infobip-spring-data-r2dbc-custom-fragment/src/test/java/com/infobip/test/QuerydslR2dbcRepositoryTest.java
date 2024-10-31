package com.infobip.test;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

@AllArgsConstructor
public class QuerydslR2dbcRepositoryTest extends TestBase {

    private final PersonRepository repository;

    @Test
    void shouldSaveWithVarArgs() {
        repository.simplePaging("");
    }
}
