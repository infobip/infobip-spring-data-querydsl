package com.infobip.spring.data.jdbc.fragment;

import static org.assertj.core.api.BDDAssertions.then;

import com.infobip.spring.data.jdbc.Person;
import com.infobip.spring.data.jdbc.QuerydslJdbcFragment;
import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

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
                ResolvableType.forClassWithGenerics(QuerydslJdbcFragment.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }

    @Test
    void repositoryShouldImplementQuerydslPredicateExecutor() {

        // when
        var actual = context.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(QuerydslPredicateExecutor.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }
}
