package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.BDDAssertions.then;

@DefaultSchema("dbo")
public class SpringDataJdbcAnnotationProcessorTest {

    @Test
    void shouldCreateQClass() {

        Path actual = Paths.get("src/test/resources/expected/QPascalCaseFooBar.java");
        Path expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QPascalCaseFooBar.java");
        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithCompileTesting() {

        // given
        SpringDataJdbcAnnotationProcessor processor = new SpringDataJdbcAnnotationProcessor();
        JavaFileObject resource = JavaFileObjects.forResource("PascalCaseFooBar.java");

        // when
        Compilation compilation = javac()
                .withProcessors(processor)
                .compile(resource);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.infobip.spring.data.jdbc.annotation.processor.QPascalCaseFooBar")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/QPascalCaseFooBar.java"));
    }

    @Test
    void shouldCreateQClassWithTableAndColumnAnnotations() {

        Path actual = Paths.get("src/test/resources/expected/QCamelCaseFooBar.java");
        Path expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QCamelCaseFooBar.java");
        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithTableAndColumnAnnotationsWithCompileTesting() {

        // given
        SpringDataJdbcAnnotationProcessor processor = new SpringDataJdbcAnnotationProcessor();
        JavaFileObject resource = JavaFileObjects.forResource("CamelCaseFooBar.java");

        // when
        Compilation compilation = javac()
                .withProcessors(processor)
                .compile(resource);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.infobip.spring.data.jdbc.annotation.processor.QCamelCaseFooBar")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/QCamelCaseFooBar.java"));
    }

    @Test
    void shouldCreateQClassWithSchema() {

        Path actual = Paths.get("src/test/resources/expected/QEntityWithSchema.java");
        Path expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QEntityWithSchema.java");

        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithSchemaWithCompileTesting() {

        // given
        SpringDataJdbcAnnotationProcessor processor = new SpringDataJdbcAnnotationProcessor();
        JavaFileObject resource = JavaFileObjects.forResource("EntityWithSchema.java");

        // when
        Compilation compilation = javac()
                .withProcessors(processor)
                .compile(resource);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.infobip.spring.data.jdbc.annotation.processor.QEntityWithSchema")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/QEntityWithSchema.java"));
    }
}
