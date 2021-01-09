package com.infobip.spring.data.jpa.fragment;

import com.infobip.spring.data.jpa.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class FragmentTest extends TestBase{

    private final ApplicationContext context;

    @Test
    void shouldInjectFragmentIntoContext() {

        // when
        String[] actual = context.getBeanNamesForType(CustomJpaFragmentPersonRepository.class);

        // then
        then(actual).isNotEmpty();
    }

    @Test
    void shouldInjectRepositoryIntoContext() {

        // when
        String[] actual = context.getBeanNamesForType(ResolvableType.forClassWithGenerics(QuerydslJpaFragment.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }
}
