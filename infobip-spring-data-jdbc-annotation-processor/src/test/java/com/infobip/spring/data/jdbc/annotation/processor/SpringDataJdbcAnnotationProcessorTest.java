package com.infobip.spring.data.jdbc.annotation.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.BDDAssertions.then;

import javax.tools.JavaFileObject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

@DefaultSchema("dbo")
public class SpringDataJdbcAnnotationProcessorTest {

    @Test
    void shouldCreateQClass() {

        Path actual = Paths.get("src/test/resources/expected/QPascalCaseFooBar.java");
        Path expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QPascalCaseFooBar.java");
        then(actual).hasSameTextualContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithCompileTesting() {

        // given
        JavaFileObject givenSource = givenSource(PascalCaseFooBar.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QPascalCaseFooBar.class);
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
        JavaFileObject givenSource = givenSource(CamelCaseFooBar.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QCamelCaseFooBar.class);
    }

    @Test
    void shouldCreateSimpleQClassWithTableAndColumnAnnotationsWithCompileTesting() {

        // given
        var givenSource = givenSource(Foo.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QFoo.class);
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
        JavaFileObject givenSource = givenSource(EntityWithSchema.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QEntityWithSchema.class);
    }

    @Test
    void shouldCreateSnakeCaseAndTransientType() {
        // given
        JavaFileObject givenSource = givenSource(SnakeCaseAndTransientType.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QSnakeCaseAndTransientType.class);
    }

    @Test
    void shouldApplyCustomCaseFormatToColumns() {
        // given
        JavaFileObject givenEntitySource = givenSource(LowerUnderScoreColumnFooBar.class);
        JavaFileObject givenConfigurationSource = givenSource(
                Paths.get("src", "test", "resources", "given", "CustomCaseFormatColumnConfiguration.java"));

        // when
        Compilation actual = whenCompile(givenConfigurationSource, givenEntitySource);

        // then
        thenShouldGenerateSourceFile(actual, QLowerUnderScoreColumnFooBar.class);
    }

    @Test
    void shouldApplyCustomCaseFormatToTable() {
        // given
        JavaFileObject givenEntitySource = givenSource(LowerUnderScoreTableFooBar.class);
        JavaFileObject givenConfigurationSource = givenSource(
                Paths.get("src", "test", "resources", "given", "CustomCaseFormatTableConfiguration.java"));

        // when
        Compilation actual = whenCompile(givenConfigurationSource, givenEntitySource);

        // then
        thenShouldGenerateSourceFile(actual, QLowerUnderScoreTableFooBar.class);
    }

    @Test
    void shouldGenerateEmbeddedQClass() {
        // given
        JavaFileObject givenSource = givenSource(EntityWithEmbedded.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QEntityWithEmbedded.class);
        thenShouldGenerateSourceFile(actual, QEmbeddedClass.class);
    }

    @Test
    void shouldIgnoreMappedCollectionAnnotatedFields() {
        // given
        JavaFileObject givenSource = givenSource(EntityWithMappedCollection.class);

        // when
        Compilation actual = whenCompile(givenSource);

        // then
       thenShouldGenerateSourceFile(actual, QEntityWithMappedCollection.class);
    }

    private void thenShouldGenerateSourceFile(Compilation actual, Class<?> typeClass) {
        assertThat(actual).succeeded();
        assertThat(actual)
                .generatedSourceFile(typeClass.getName())
                .hasSourceEquivalentTo(JavaFileObjects.forResource("expected/" + typeClass.getSimpleName() + ".java"));
    }

    private JavaFileObject givenSource(Class<?> type) {
        return givenSource(Paths.get("src", "test", "java", "com", "infobip", "spring", "data", "jdbc", "annotation",
                                     "processor",
                                     type.getSimpleName() + ".java"));
    }

    private JavaFileObject givenSource(Path path) {
        return JavaFileObjects.forResource(convert(path.toUri()));
    }

    private URL convert(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Compilation whenCompile(JavaFileObject... files) {
        return javac().withProcessors(new SpringDataJdbcAnnotationProcessor())
                      .compile(files);
    }
}
