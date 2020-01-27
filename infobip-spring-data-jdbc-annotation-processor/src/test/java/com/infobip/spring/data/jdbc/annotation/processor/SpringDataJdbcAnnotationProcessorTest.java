package com.infobip.spring.data.jdbc.annotation.processor;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.BDDAssertions.then;

public class SpringDataJdbcAnnotationProcessorTest {

    @Test
    void shouldCreateQClass() {

        Path actual = Paths.get("src/test/resources/expected/QPascalCaseFooBar.java");
        Path expected = Paths.get("target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QPascalCaseFooBar.java");
        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithTableAndColumnAnnotations() {

        Path actual = Paths.get("src/test/resources/expected/QCamelCaseFooBar.java");
        Path expected = Paths.get("target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QCamelCaseFooBar.java");
        then(actual).hasSameContentAs(expected);
    }
}