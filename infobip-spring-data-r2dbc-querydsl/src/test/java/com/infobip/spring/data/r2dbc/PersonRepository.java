package com.infobip.spring.data.r2dbc;

import reactor.core.publisher.Flux;

import java.util.List;

public interface PersonRepository extends QuerydslR2dbcRepository<Person, Long> {

    Flux<Person> firstNameIn(List<String> names);
}
