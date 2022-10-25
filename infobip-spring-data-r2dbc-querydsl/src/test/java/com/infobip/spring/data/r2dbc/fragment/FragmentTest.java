package com.infobip.spring.data.r2dbc.fragment;

import static org.assertj.core.api.BDDAssertions.then;

import com.infobip.spring.data.r2dbc.Person;
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment;
import com.infobip.spring.data.r2dbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;

@AllArgsConstructor
public class FragmentTest extends TestBase {

    private final ApplicationContext context;

    @Test
    void shouldInjectFragmentIntoContext() {

        // when
        var actual = context.getBeanNamesForType(CustomFragmentPersonRepository.class);

        // then
        then(actual).isNotEmpty();
    }

    @Test
    void shouldInjectRepositoryIntoContext() {

        // when
        var actual = context.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(QuerydslR2dbcFragment.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }

    @Test
    void repositoryShouldImplementQuerydslPredicateExecutor() {

        // when
        var actual = context.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(ReactiveQuerydslPredicateExecutor.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }
}
