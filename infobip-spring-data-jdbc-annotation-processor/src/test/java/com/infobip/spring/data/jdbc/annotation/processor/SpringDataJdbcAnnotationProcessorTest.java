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

        var actual = Paths.get("src/test/resources/expected/QPascalCaseFooBar.java");
        var expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QPascalCaseFooBar.java");
        then(actual).hasSameTextualContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithCompileTesting() {

        // given
        var givenSource = givenSource(PascalCaseFooBar.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QPascalCaseFooBar.class);
    }

    @Test
    void shouldCreateQClassWithTableAndColumnAnnotations() {

        var actual = Paths.get("src/test/resources/expected/QCamelCaseFooBar.java");
        var expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QCamelCaseFooBar.java");
        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithTableAndColumnAnnotationsWithCompileTesting() {

        // given
        var givenSource = givenSource(CamelCaseFooBar.class);

        // when
        var actual = whenCompile(givenSource);

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
    void shouldCreateNestedQClassWithTableAndColumnAnnotationsWithCompileTesting() {

        // given
        var givenSource = givenSource(Nest.NestedFoo.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QNest_NestedFoo.class);
    }

    @Test
    void shouldCreateSimpleQClassWithTableNameAndColumnAnnotationsWithCompileTesting() {

        // given
        var givenSource = givenSource(FooWithTableName.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QFooWithTableName.class);
    }

    @Test
    void shouldCreateQClassWithSchema() {

        var actual = Paths.get("src/test/resources/expected/QEntityWithSchema.java");
        var expected = Paths.get(
                "target/generated-test-sources/test-annotations/com/infobip/spring/data/jdbc/annotation/processor/QEntityWithSchema.java");

        then(actual).hasSameContentAs(expected);
    }

    @Test
    void shouldCreateQClassWithSchemaWithCompileTesting() {

        // given
        var givenSource = givenSource(EntityWithSchema.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QEntityWithSchema.class);
    }

    @Test
    void shouldCreateSnakeCaseAndTransientType() {
        // given
        var givenSource = givenSource(SnakeCaseAndTransientType.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QSnakeCaseAndTransientType.class);
    }

    @Test
    void shouldApplyCustomCaseFormatToColumns() {
        // given
        var givenEntitySource = givenSource(LowerUnderScoreColumnFooBar.class);
        var givenConfigurationSource = givenSource(
                Paths.get("src", "test", "resources", "given", "CustomCaseFormatColumnConfiguration.java"));

        // when
        var actual = whenCompile(givenConfigurationSource, givenEntitySource);

        // then
        thenShouldGenerateSourceFile(actual, QLowerUnderScoreColumnFooBar.class);
    }

    @Test
    void shouldApplyCustomCaseFormatToTable() {
        // given
        var givenEntitySource = givenSource(LowerUnderScoreTableFooBar.class);
        var givenConfigurationSource = givenSource(
                Paths.get("src", "test", "resources", "given", "CustomCaseFormatTableConfiguration.java"));

        // when
        var actual = whenCompile(givenConfigurationSource, givenEntitySource);

        // then
        thenShouldGenerateSourceFile(actual, QLowerUnderScoreTableFooBar.class);
    }

    @Test
    void shouldGenerateEmbeddedQClass() {
        // given
        var givenSource = givenSource(EntityWithEmbedded.class);

        // when
        var actual = whenCompile(givenSource);

        // then
        thenShouldGenerateSourceFile(actual, QEntityWithEmbedded.class);
        thenShouldGenerateSourceFile(actual, QEmbeddedClass.class);
    }

    @Test
    void shouldIgnoreMappedCollectionAnnotatedFields() {
        // given
        var givenSource = givenSource(EntityWithMappedCollection.class);

        // when
        var actual = whenCompile(givenSource);

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
