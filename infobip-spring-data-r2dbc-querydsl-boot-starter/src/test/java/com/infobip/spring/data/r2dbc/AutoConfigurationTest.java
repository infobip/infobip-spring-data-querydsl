package com.infobip.spring.data.r2dbc;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestConstructor;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(PER_CLASS)
@SpringBootTest(classes = Main.class)
@AllArgsConstructor
public class AutoConfigurationTest {

    private final ApplicationContext context;

    @Test
    void shouldCreateCustomBaseRepository() {

        // when
        var actual = context.getBean(PersonRepository.class);

        // then
        then(actual).isInstanceOf(QuerydslR2dbcRepository.class);
    }
}
