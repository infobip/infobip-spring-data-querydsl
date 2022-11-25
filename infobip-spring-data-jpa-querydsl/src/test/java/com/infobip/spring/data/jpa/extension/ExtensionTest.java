package com.infobip.spring.data.jpa.extension;

import static org.assertj.core.api.BDDAssertions.then;

import com.infobip.spring.data.jpa.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class ExtensionTest extends TestBase {

    private final ApplicationContext context;

    @Test
    void shouldCreateCustomBaseRepository() {

        // when
        var actual = context.getBeanNamesForType(CustomExtensionPersonRepository.class);

        // then
        then(actual).isNotEmpty();
    }
}
