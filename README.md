# Infobip Spring Data Querydsl

[![](https://github.com/infobip/infobip-spring-data-querydsl/workflows/maven/badge.svg)](https://github.com/infobip/infobip-spring-data-querydsl/actions?query=workflow%3Amaven)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-querydsl/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-querydsl)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-spring-data-querydsl/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-spring-data-querydsl?branch=master)
[![Known Vulnerabilities](https://snyk.io/test/github/infobip/infobip-spring-data-querydsl/badge.svg)](https://snyk.io/test/github/infobip/infobip-spring-data-querydsl)

Infobip Spring Data Querydsl provides new functionality that enables the user to leverage the full power of Querydsl API on top of Spring Data repository infrastructure.

## Contents

1. [Changelog](#Changelog)
1. [Note on general usage](#NoteOnGeneralUsage)
1. [JDBC module:](#JDBC)
    * [Requirements](#JDBCRequirements)
    * [Setup](#JDBCSetup)
    * [Features and examples](#JDBCFeaturesAndExamples)
        * [Inner Join](#JDBCInnerJoin)
        * [Projections](#JDBCProjections)
        * [Query](#JDBCQuery)
        * [Update](#JDBCUpdate)
        * [Delete](#JDBCDelete)
        * [Transactional support](#JDBCTransactionalSupport)
        * [Embedded support](#JDBCEmbeddedSupport)
        * [@MappedCollection](#MappedCollection)
        * [Streaming](#JDBCStreaming)
    * [Extension](#JDBCExtension)
1. [R2DBC module:](#R2DBC)
   * [Requirements](#R2DBCRequirements)
   * [Setup](#R2DBCSetup)
   * [Features and examples](#R2DBCFeaturesAndExamples)
      * [Inner Join](#R2DBCInnerJoin)
      * [Projections](#R2DBCProjections)
      * [Query](#R2DBCQuery)
      * [Update](#R2DBCUpdate)
      * [Delete](#R2DBCDelete)
      * [Transactional support](#R2DBCTransactionalSupport)
   * [Extension](#R2DBCExtension)
1. [JPA module:](#JPA)
    * [Requirements](#JPARequirements)
    * [Setup](#JPASetup)
    * [Features and examples:](#JPAFeaturesAndExamples)
        * [Native queries with Querydsl](#JPANativeQueriesWithQuerydsl)
        * [Projections](#JPAProjections)
        * [Query](#JPAQuery)
        * [Update](#JPAUpdate)
        * [Delete](#JPADelete)
        * [List instead of Iterable return type](#JPAListInsteadOfIterableReturnType)
        * [Transactional support](#JPATransactionalSupport)
        * [Stored procedure builder](#JPAStoredProcedureBuilder)
        * [Streaming](#JPAStreaming)
    * [Extension](#JPAExtension)
1. [Annotation processor](#AnnotationProcessor)
1. [Further reading](#FurtherReading)
1. [Running tests](#RunningTests)
1. [Contributing](#Contributing)
1. [License](#License)

## <a name="Changelog"></a> Changelog

For changes check the [changelog](CHANGELOG.md).

## <a name="NoteOnGeneralUsage"></a> Note on general usage

For the sake of brevity, all examples use repository methods directly.

In production code persistence layer (SQL) shouldn't leak to service layer.
See [this answer](https://stackoverflow.com/a/26563841/607767) by Oliver Drotbohm (Spring Data Project Lead @ Pivotal) on how to approach encapsulating persistence logic.

## <a name="JDBC"></a> JDBC module:

### <a name="JDBCRequirements"></a> Requirements:

- Java 8 with [parameter names preserved in byte code](https://stackoverflow.com/a/20594685/607767) (used to map columns to constructor parameters)
- Spring Data JDBC
- entities must have an all argument constructor (`@AllArgsConstructor`), can have others as well
- entity class and all argument constructor must be public (limitation of Querydsl)

### <a name="JDBCSetup"></a> Setup:

1. Dependency:

```xml
<dependency>
   <groupId>com.infobip</groupId>
   <artifactId>infobip-spring-data-jdbc-querydsl-boot-starter</artifactId>
   <version>${infobip-spring-data-jdbc-querydsl.version}</version>
</dependency>
```

2. Refactor repository interfaces to either use new base repository or fragments approach:

* new base repository approach:   

```java
interface TRepository extends QuerydslJdbcRepository<T, ID> {
}
```

* fragments:

```java
interface TRepository extends PagingAndSortingRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJdbcFragment<T> {
}
```

3. Done

### <a name="JDBCFeaturesAndExamples"></a> Features and examples:

All examples have corresponding tests in the project and can be found [here](infobip-spring-data-jdbc-querydsl/src/test/java/com/infobip/spring/data/jdbc).

#### <a name="JDBCInnerJoin"></a> Inner Join:

Inner join example:

```
List<Person> actual = repository.query(query -> query
        .select(repository.entityProjection())
        .from(person)
        .innerJoin(personSettings)
        .on(person.id.eq(personSettings.personId))
        .where(personSettings.id.eq(johnDoeSettings.getId()))
        .fetch());
);
```

#### <a name="JDBCProjections"></a> Projections

For examples how to construct projections refer to the official documentation - [section result handling](http://www.querydsl.com/static/querydsl/latest/reference/html_single/#result_handling).

Here is an example that uses constructor:

```$xslt
@Value
public static class PersonProjection {
    private final String firstName;
    private final String lastName;
}
...

List<PersonProjection> actual = repository.query(query -> query
        .select(Projections.constructor(PersonProjection.class, person.firstName,
                                        person.lastName))
        .from(person)
        .fetch());
```

#### <a name="JDBCQuery"></a> Query

```
Optional<Person> actual = repository.queryOne(query -> query
        .select(repository.entityProjection())
        .from(person)
        .where(person.firstName.in("John", "Jane"))
        .orderBy(person.firstName.asc(), person.lastName.asc())
        .limit(1)
        .offset(1));
```

```
List<Person> actual = repository.queryMany(query -> query
        .select(repository.entityProjection())
        .from(person)
        .where(person.firstName.in("John", "Jane"))
        .orderBy(person.firstName.asc(), person.lastName.asc())
        .limit(1)
        .offset(1));
```

#### <a name="JDBCUpdate"></a> Update

```
repository.update(query -> query
        .set(person.firstName, "John")
        .where(person.firstName.eq("Johny"))
        .execute());
```

#### <a name="JDBCDelete"></a> Delete

```
long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));
```

#### <a name="JDBCTransactionalSupport"></a> Transactional support

Queries execution is always done inside the repository implementation (loan pattern) in a transaction so transactions don't have to be 
handled manually (like they do if you are manually managing SQLQuery and other Querydsl constructs).

#### <a name="JDBCEmbeddedSupport"></a> Embedded support

Entity fields marked with `@org.springframework.data.relational.core.mapping.Embedded` are inlined in Q classes:

Model:
```java
@Table("Person")
@Value
public class PersonWithEmbeddedFirstAndLastName {

   @With
   @Id
   private final Long id;

   @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY)
   private final FirstAndLastName firstAndLastName;
   ...
}
```


```java
@Value
public class FirstAndLastName {

    private final String firstName;
    private final String lastName;
}
```

Query (note the missing .personWithEmbeddedFirstAndLastName field in Q instance):

```java
repository.findAll(personWithEmbeddedFirstAndLastName.firstName.in("John", "Johny"));
```

#### <a name="MappedCollection"></a> @MappedCollection support

Model:
```java
@Value
public class Student {

   @Id
   Long id;

   String name;

   @MappedCollection(idColumn = "StudentId", keyColumn = "CourseId")
   Set<StudentCourse> courses;

   void addItem(Course course) {
      StudentCourse studentCourse = new StudentCourse(null, AggregateReference.to(course.getId()), null);
      courses.add(studentCourse);
   }
}
```

```java
@Value
public class Course {

   @Id
   Long id;

   String name;
}
```

```java
@Value
public class StudentCourse {

   @Id
   Long id;

   AggregateReference<Course,Long> courseId;

   Long studentId;
}
```

Query:

```java
List<Student> actual = studentRepository.query(query -> query.select(studentRepository.entityProjection())
                                                             .from(student)
                                                             .innerJoin(studentCourse)
                                                             .on(student.id.eq(studentCourse.studentId))
                                                             .fetch());
```

#### <a name="JDBCStreaming"></a> Streaming

`streamAll` is a new method added to repository for more convenient use.

```
@Transactional
public void transactionalAnnotatedMethodRequiredForConsumingStream() {
   try (Stream<Person> stream = repository.streamAll()) {
      // use stream
   }
}
```

### <a name="JDBCExtension"></a> Extension:

To create a custom base repository interface you'll need to create:
*  custom base interface
*  custom annotation for enabling
*  custom factory bean class and potentially factory class depending on requirements

Take a look at [extension package in tests](infobip-spring-data-jdbc-querydsl/src/test/java/com/infobip/spring/data/jdbc/extension) as an example on how this can be achieved.

## <a name="R2DBC"></a> R2DBC module:

### <a name="R2DBCRequirements"></a> Requirements:

- Java 8 with [parameter names preserved in byte code](https://stackoverflow.com/a/20594685/607767) (used to map columns to constructor parameters)
- Spring Data R2DBC
- entities must have an all argument constructor (`@AllArgsConstructor`), can have others as well
- entity class and all argument constructor must be public (limitation of Querydsl)
- if you're not using Flyway, you need to provide a SQLTemplates bean

### <a name="R2DBCSetup"></a> Setup:

1. Dependency:

```xml
<dependency>
   <groupId>com.infobip</groupId>
   <artifactId>infobip-spring-data-r2dbc-querydsl-boot-starter</artifactId>
   <version>${infobip-spring-data-r2dbc-querydsl.version}</version>
</dependency>
```

2. Refactor repository interfaces to either use new base repository or fragments approach:

* new base repository approach:

```java
interface TRepository extends QuerydslR2dbcRepository<T, ID> {
}
```

* fragments:

```java
interface TRepository extends ReactiveSortingRepository<T, ID>, ReactiveQuerydslPredicateExecutor<T>, QuerydslR2dbcFragment<T> {
}
```

3. Done

### <a name="R2DBCFeaturesAndExamples"></a> Features and examples:

All examples have corresponding tests in the project and can be found [here](infobip-spring-data-r2dbc-querydsl/src/test/java/com/infobip/spring/data/r2dbc).

#### <a name="R2DBCInnerJoin"></a> Inner Join:

Inner join example:

```
Flux<Person> actual = repository.query(query -> query.select(repository.entityProjection())
                                                          .from(person)
                                                          .innerJoin(personSettings)
                                                          .on(person.id.eq(personSettings.personId))
                                                          .where(personSettings.id.eq(johnDoeSettings.getId())))
                                     .all();
```

#### <a name="R2DBCProjections"></a> Projections

For examples how to construct projections refer to the official documentation - [section result handling](http://www.querydsl.com/static/querydsl/latest/reference/html_single/#result_handling).

Here is an example that uses constructor:

```$xslt
@Value
public static class PersonProjection {
    private final String firstName;
    private final String lastName;
}
...

Flux<PersonProjection> actual = repository.query(query -> query
        .select(constructor(PersonProjection.class, person.firstName, person.lastName))
        .from(person))
                                          .all();
```

#### <a name="R2DBCQuery"></a> Query

```
Flux<Person> actual = repository.query(query -> query.select(repository.entityProjection())
                                                     .from(person)
                                                     .where(person.firstName.in("John", "Jane"))
                                                     .orderBy(person.firstName.asc(),
                                                              person.lastName.asc())
                                                     .limit(1)
                                                     .offset(1))
                                .all();
```

#### <a name="R2DBCUpdate"></a> Update

```
Mono<Integer> numberOfAffectedRows = repository.update(query -> query.set(person.firstName, "John")
                                                                     .where(person.firstName.eq("Johny")));
```

#### <a name="R2DBCDelete"></a> Delete

```
Mono<Integer> numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));
```

#### <a name="R2DBCTransactionalSupport"></a> Transactional support

Queries execution is always done inside the repository implementation (loan pattern) in a transaction so transactions don't have to be
handled manually (like they do if you are manually managing SQLQuery and other Querydsl constructs).

### <a name="R2DBCExtension"></a> Extension:

To create a custom base repository interface you'll need to create:
*  custom base interface
*  custom annotation for enabling
*  custom factory bean class and potentially factory class depending on requirements

Take a look at [extension package in tests](infobip-spring-data-r2dbc-querydsl/src/test/java/com/infobip/spring/data/r2dbc/extension) as an example on how this can be achieved.


## <a name="JPA"></a> JPA module:

### <a name="JPARequirements"></a> Requirements:

- Java 8
- Spring Data JPA

### <a name="JPASetup"></a> Setup:

1. Dependency:

```xml
<dependency>
   <groupId>com.infobip</groupId>
   <artifactId>infobip-spring-data-jpa-querydsl-boot-starter</artifactId>
   <version>${infobip-spring-data-jpa-querydsl.version}</version>
</dependency>
```

As this project depends on querydsl-apt with jpa classifier you don't need to set up explicit Maven build phase for Q classes generation.
For building Q classes without Maven, make sure your IDE has Annotation processing enabled.

2. Refactor repository interfaces to either use new base repository or fragments approach:

* new base repository approach:

```java
interface TRepository extends ExtendedQueryDslJpaRepository<T, ID> {
}
```

* fragments:

```java
interface TRepository extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T>, QuerydslJpaFragment<T> {
}
```

3. Done

If you need other features from `@EnableJpaRepositories` you can use:

```
@EnableJpaRepositories(repositoryFactoryBeanClass = ExtendedQuerydslJpaRepositoryFactoryBean.class)
```

### <a name="JPAFeaturesAndExamples"></a> Features and examples:

All examples have corresponding tests in the project and can be found [here](infobip-spring-data-jpa-querydsl/src/test/java/com/infobip/spring/data/jpa).

#### <a name="JPANativeQueriesWithQuerydsl"></a> Native queries with Querydsl:

Example which uses union clause (unions aren't available in JPA):

```
List<Person> actual = repository.jpaSqlQuery(query -> query
        .union(
                repository.jpaSqlSubQuery(subQuery ->
                                                  subQuery.select(person)
                                                          .from(person)
                                                          .where(person.firstName.like("John"))),
                repository.jpaSqlSubQuery(subQuery ->
                                                  subQuery.select(person)
                                                          .from(person)
                                                          .where(person.firstName.like("Jan%")))
        )
        .orderBy(person.firstName.asc(), person.lastName.asc())
        .fetch()
);
```

#### <a name="JPAProjections"></a> Projections

For examples how to construct projections refer to the official documentation - [section result handling](http://www.querydsl.com/static/querydsl/latest/reference/html_single/#result_handling).

Here is an example that uses constructor:

```$xslt
@Value
public class PersonProjection {
    private final String firstName;
    private final String lastName;
}
...
 
List<PersonProjection> actual = repository.query(query -> query
                                          .select(Projections.constructor(PersonProjection.class, person.firstName, person.lastName))
                                          .from(person)
                                          .fetch());
```

#### <a name="JPAQuery"></a> Query

Query exposes full API of JPAQuery ([QueryDslPredicateExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html) 
only exposes where clause (Predicate) and order clause (OrderSpecifier)).

This along with Querydsl 4 API improvement can lead to code that looks more like regular SQL:

```
List<Person> actual = repository.query(query -> query
        .select(person)
        .from(person)
        .where(person.firstName.in("John", "Jane"))
        .orderBy(person.firstName.asc(), person.lastName.asc())
        .limit(1)
        .offset(1)
        .fetch());
```

#### <a name="JPAUpdate"></a> Update

```
repository.update(query -> query
        .set(person.firstName, "John")
        .where(person.firstName.eq("Johny"))
        .execute());
```

#### <a name="JPADelete"></a> Delete

```
long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));
```

#### <a name="JPAListInsteadOfIterableReturnType"></a> List instead of Iterable return type

[QueryDslPredicateExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html)#findAll methods return Iterable which can be cumbersome to use. 
Those methods were overridden and now return a List which is easier to use and is easier to convert to Stream.

#### <a name="JPATransactionalSupport"></a> Transactional support

Query execution is always done inside the repository implementation (loan pattern) in a transaction so transactions don't have to be 
handled manually (like they do if you are manually managing JPAQuery and other Querydsl constructs).

#### <a name="JPAStoredProcedureBuilder"></a> Stored procedure builder

JPA support for stored procedures is quite cumbersome and it also requires a reference to EntityManager which leads to code like this:

```
@PersistenceContext
private EntityManager entityManager
...
 
@SuppressWarnings("unchecked")
public List<Person> delete(Person personToDelete) {
    return (List<Person>) entityManager
            .createStoredProcedureQuery("Person_Delete")
            .registerStoredProcedureParameter("FirstName", String.class, ParameterMode.IN)
            .registerStoredProcedureParameter("LastName", String.class, ParameterMode.IN)
            .setParameter("FirstName", personToDelete.getFirstName())
            .setParameter("LastName", personToDelete.getLastName())
            .getResultList(); // returns untyped List => unchecked
}
```

For this case, executeStoredProcedure method was added which supports Q class attributes:

```
public List<Person> delete(Person personToDelete) {
    return repository.executeStoredProcedure(
            "Person_Delete",
            builder -> builder.addInParameter(person.firstName, personToDelete.getFirstName())
                              .addInParameter(person.lastName, personToDelete.getLastName())
                              .getResultList());
}
```

#### <a name="JPAStreaming"></a> Streaming

`streamAll` is a new method added to repository for more convenient use.

```
@Transactional
public void transactionalAnnotatedMethodRequiredForConsumingStream() {
   try (Stream<Person> stream = repository.streamAll()) {
      // use stream
   }
}
```

### <a name="JPAExtension"></a> Extension:

To create a custom base repository interface you'll need to create:
*  custom base interface
*  custom annotation for enabling
*  custom factory bean class and potentially factory class depending on requirements

Take a look at [extension package in tests](infobip-spring-data-jpa-querydsl/src/test/java/com/infobip/spring/data/jpa/extension) as an example on how this can be achieved.

## <a name="AnnotationProcessor"></a> Annotation processor

Annotation processor [infobip-spring-data-jdbc-annotation-processor](infobip-spring-data-jdbc-annotation-processor) is used by R2DBC and JDBC modules to generate Querydsl Q classes.
Without annotation processor this process can be quite cumbersome as connecting to database would be required during the build phase.

Annotation processor generates Q classes for all classes that have `@Id` annotated fields. 
Reason why `@Id` is used and not some custom annotation is for simplicity of use and implementation and because `@Id` is [required by Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.id-generation):
> Spring Data JDBC uses the ID to identify entities. The ID of an entity must be annotated with Spring Data’s @Id annotation.

Current implementation of Annotation Processor uses pascal casing based naming strategy for table and column names.

To customize this behavior across whole project add following annotation to one of your classes:
```
@ProjectTableCaseFormat(CaseFormat.LOWER_UNDERSCORE)
@ProjectColumnCaseFormat(CaseFormat.LOWER_UNDERSCORE)
public class SomeClassOnCompilationPath {
...
}
```
`SomeClassOnCompilationPath` can be any class that is being compiled in the project. 

Note that for customizing single table/column mapping [Table](https://docs.spring.io/spring-data/jdbc/docs/2.1.8/api/org/springframework/data/relational/core/mapping/Table.html) and [Column](https://docs.spring.io/spring-data/jdbc/docs/2.1.8/api/org/springframework/data/relational/core/mapping/Column.html) can be used.

If this behavior needs to be changed across multiple projects, or you simply wish to customize annotation processor following steps can be taken:
1. create a new Maven module (or a Maven project if you want to reuse across multiple projects)
1. add dependency to `infobip-spring-data-jdbc-annotation-processor-common`
1. create implementation of `com.querydsl.sql.codegen.NamingStrategy`
1. create annotation processor that extends the base one:
```java
@AutoService(Processor.class)
public class CustomSpringDataJdbcAnnotationProcessor extends SpringDataJdbcAnnotationProcessorBase {

    public CustomSpringDataJdbcAnnotationProcessor() {
        super(CustomNamingStrategy.class);
    }
}
```
1. in module (or project) that needs to use this new processor exclude the default annotation processor dependency and include your own:
```xml
<dependency>
	<groupId>com.infobip</groupId>
    <!-- infobip-spring-data-jdbc-querydsl-boot-starter is used as an example here, same pattern applies for other modules --> 
	<artifactId>infobip-spring-data-jdbc-querydsl-boot-starter</artifactId>
	<version>${infobip-spring-data-jdbc-querydsl-boot-starter.version}</version>
	<exclusions>
		<exclusion>
			<groupId>com.infobip</groupId>
			<artifactId>infobip-spring-data-jdbc-annotation-processor</artifactId>
		</exclusion>
	</exclusions>
</dependency>

<!-- include dependency to custom annotation processor -->
```

[infobip-spring-data-jdbc-annotation-processor](infobip-spring-data-jdbc-annotation-processor) can be used as an example codebase for custom annotation processor. 
It includes tests that can be used for custom annotation processor as well.

In case you want to manually generate Q classes you can still exclude `infobip-spring-data-jdbc-annotation-processor` and do the process manually (e.g. like [this](https://github.com/infobip/infobip-spring-data-querydsl/commit/9b41403bdea38672caa5a4c57427cdcc2ef8c2a7#diff-ca2587b532ca6c66340cb5032feded4e6b090942f295556d27b480a81d417ba2)).


## <a name="FurtherReading"></a> Further reading

- [Querydsl documentation](http://www.querydsl.com/static/querydsl/latest/reference/html_single/)
- [Atlassian Querydsl examples](https://bitbucket.org/atlassian/querydsl-examples)
- [Querydsl google group](https://groups.google.com/forum/#!forum/querydsl)
- [Spring Data JPA documentation](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [QueryDSL-EntityQL](https://github.com/eXsio/querydsl-entityql)

## <a name="RunningTests"></a> Running tests

To run tests you need to have docker installed.
Containers are automatically started using [testcontainers](https://github.com/infobip/infobip-testcontainers-spring-boot-starter).

## <a name="Contributing"></a> Contributing

If you have an idea for a new feature or want to report a bug please use the issue tracker.

Pull requests are welcome!

## <a name="License"></a> License

This library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
