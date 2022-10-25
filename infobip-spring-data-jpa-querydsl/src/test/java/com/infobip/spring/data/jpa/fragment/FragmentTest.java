package com.infobip.spring.data.jpa.fragment;

import static org.assertj.core.api.BDDAssertions.then;

import com.infobip.spring.data.jpa.Person;
import com.infobip.spring.data.jpa.QuerydslJpaFragment;
import com.infobip.spring.data.jpa.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

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
                ResolvableType.forClassWithGenerics(QuerydslJpaFragment.class, Person.class));

        // then
        then(actual).isNotEmpty();
    }
}
