# Infobip Spring Data Querydsl

![](https://github.com/infobip/infobip-spring-data-querydsl/workflows/maven/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-querydsl/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-querydsl)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-spring-data-querydsl/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-spring-data-querydsl?branch=master)

Infobip Spring Data Querydsl provides new functionality that enables the user to leverage the full power of Querydsl API on top of Spring Data repository infrastructure.

The project is divided into 2 modules: infobip-spring-data-jdbc-querydsl and infobip-spring-data-jpa-querydsl.

## Contents

1. [News](#News)
2. [JDBC module:](#JDBC)
    * [Requirements](#JDBCRequirements)
    * [Setup](#JDBCSetup)
    * [Features and examples](#JDBCFeaturesAndExamples)
        * [Annotation Processor](#AnnotationProcessor)
        * [Inner Join](#InnerJoin)
        * [Projections](#Projections)
        * [Query](#Query)
        * [Update](#Update)
        * [Delete](#Delete)
        * [Transactional support](#TransactionalSupport)
3. [JPA module:](#JPA)
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
4. [Domain Driven Design concerns](#DomainDrivenDesignConcerns)
5. [Further reading](#FurtherReading)
6. [Running tests](#RunningTests)
7. [Contributing](#Contributing)
8. [License](#License)

## <a name="News"></a> News

### 4.1.0

   * `QuerydslJdbcRepository` now extends `PagingAndSortingRepository`

### 4.0.0

* Breaking change:
    * removed second generic parameter from QuerydslJdbcRepository

### 3.0.0

* Breaking changes: 
    * renamed `@EnableExtendedRepositories` to `@EnableExtendedJpaRepositories`
    * renamed `ExtendedQueryDslJpaRepository` to `ExtendedQuerydslJpaRepository`
* Added new module - infobip-spring-data-jdbc-querydsl.

## <a name="JDBC"></a> JDBC module:

## <a name="JDBCRequirements"></a> Requirements:

- Java 8 with [parameter names preserved in byte code](https://stackoverflow.com/a/20594685/607767) (used to map columns to constructor parameters)
- Spring Data JDBC
- Querydsl

### <a name="JDBCSetup"></a> Setup:

1. Dependency:

```xml
<dependency>
   <groupId>com.infobip</groupId>
   <artifactId>infobip-spring-data-jdbc-querydsl</artifactId>
   <version>${infobip-spring-data-jdbc-querydsl.version}</version>
</dependency>
```

2. Add `@EnableQuerydslJdbcRepositories` to your Main class:

```java
@EnableQuerydslJdbcRepositories // replaces @EnableJdbcRepositories
@SpringBootApplication
public class Main {
 
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).run(args);
    }
}
```

3. Refactor repository interfaces to use `QuerydslJdbcRepository` instead of `CrudRepository`:

```java
interface FooRepository extends QuerydslJdbcRepository<Foo, ID> {
}
```

4. Done

### <a name="JDBCFeaturesAndExamples"></a> Features and examples:

All examples have corresponding tests in the project and can be found [here](infobip-spring-data-jdbc-querydsl/src/test/java/com/infobip/spring/data/jdbc/QuerydslJdbcRepositoryTest.java).

#### <a name="AnnotationProcessor"></a> Annotation Processor:

`infobip-spring-data-jdbc-annotation-processor` provides an annotation processor that automatically generates Q classes without connecting to the database.

`infobip-spring-data-jdbc-querydsl` depends on `infobip-spring-data-jdbc-annotation-processor` so you don't need to add explicit dependency.

In case you want to manually generate Q classes you can still exclude `infobip-spring-data-jdbc-annotation-processor` and do the process manually (e.g. like [this](https://github.com/infobip/infobip-spring-data-querydsl/commit/9b41403bdea38672caa5a4c57427cdcc2ef8c2a7#diff-ca2587b532ca6c66340cb5032feded4e6b090942f295556d27b480a81d417ba2)). 

#### <a name="InnerJoin"></a> Inner Join:

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

#### <a name="Projections"></a> Projections

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

#### <a name="Query"></a> Query

```
List<Person> actual = repository.query(query -> query
        .select(repository.entityProjection())
        .from(person)
        .where(person.firstName.in("John", "Jane"))
        .orderBy(person.firstName.asc(), person.lastName.asc())
        .limit(1)
        .offset(1)
        .fetch());
```

#### <a name="Update"></a> Update

```
repository.update(query -> query
        .set(person.firstName, "John")
        .where(person.firstName.eq("Johny"))
        .execute());
```

#### <a name="Delete"></a> Delete

```
long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));
```

#### <a name="TransactionalSupport"></a> Transactional support

Queries execution is always done inside the repository implementation (loan pattern) in a transaction so transactions don't have to be 
handled manually (like they do if you are manually managing SQLQuery and other Querydsl constructs).

## <a name="JPA"></a> JPA module:

## <a name="JPARequirements"></a> Requirements:

- Java 8
- Hibernate (if you need support for other JPA implementors please open an issue)
- Spring Data JPA
- Querydsl

### <a name="JPASetup"></a> Setup:

1. Dependency:

```xml
<dependency>
   <groupId>com.infobip</groupId>
   <artifactId>infobip-spring-data-jpa-querydsl</artifactId>
   <version>${infobip-spring-data-jpa-querydsl.version}</version>
</dependency>
```

As this project depends on querydsl-apt with jpa classifier you don't need to set up explicit Maven build phase for Q classes generation.
For building Q classes without Maven, make sure your IDE has Annotation processing enabled.

2. Add @EnableExtendedJpaRepositories to your Main class:

```java
@EnableExtendedJpaRepositories // replaces @EnableJpaRepositories
@SpringBootApplication
public class Main {
 
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).run(args);
    }
}
```

3. Refactor repository interfaces to use `ExtendedQueryDslJpaRepository` instead of `JpaRepository` and `QueryDslPredicateExecutor` (note that ExtendedQueryDslJpaRepository extends and provides the API of both):

```java
// ExtendedQueryDslJpaRepository replaces both JpaRepository and QueryDslPredicateExecutor
interface FooRepository extends ExtendedQueryDslJpaRepository<Foo, ID> {
}
```

4. Done

If you need other features from `@EnableJpaRepositories` you can use:

```
@EnableJpaRepositories(repositoryBaseClass = SimpleExtendedQueryDslJpaRepository.class)
```

### <a name="JPAFeaturesAndExamples"></a> Features and examples:

All examples have corresponding tests in the project and can be found [here](infobip-spring-data-jpa-querydsl/src/test/java/com/infobip/spring/data/jpa/ExtendedQuerydslJpaRepositoryTest.java).

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

## <a name="DomainDrivenDesignConcerns"></a> Domain Driven Design concerns

In following example one could argue that database related logic has leaked from repository to service layer:

```java
class FooService {

    private final FooRepository repository;
    
    ...
    
    List<Foo> findAll(String barName, Long limit, Long offset) {
        
        ...
        
        return repository.query(query -> query.select(foo)
                                              .from(foo)
                                              .where(foo.bar.name.eq(barName))
                                              .limit(limit)
                                              .offset(offset)
                                              .fetch());
    }
}
```

In order to prevent this, you can [customize the repository](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior).

First, create a custom repository:

```java
interface FooCustomRepository {

    List<Foo> findAll(String barName, Long limit, Long offset);
}
```

Make `FooRepository` extend `FooCustomRepository`:

```java
interface FooRepository extends ExtendedQueryDslJpaRepository<Foo, ID>, FooCustomRepository {
}
```

Provide an implementation for `FooCustomRepository`:

```java
class FooCustomRepositoryImpl implements FooCustomRepository {

    private final ExtendedQueryDslJpaRepository<Foo, ID> repository;

    FooCustomRepositoryImpl(@Lazy ExtendedQueryDslJpaRepository<Foo, ID> repository) {
        this.repository = repository;
    }
    
    @Override
    public List<Foo> findAll(String barName, Long limit, Long offset) {
        return repository.query(query -> query.select(foo)
                                              .from(foo)
                                              .where(foo.bar.name.eq(barName))
                                              .limit(limit)
                                              .offset(offset)
                                              .fetch());
    }
}
```

Refactor service layer to use the new method:

```java
class FooService {

    private final FooRepository repository;
    
    ...
    
    List<Foo> findAll(String barName, Long limit, Long offset) {
        
        ...
        
        return repository.findAll(barName, limit, offset);
    }
}
```

## <a name="FurtherReading"></a> Further reading

- [Querydsl documentation](http://www.querydsl.com/static/querydsl/latest/reference/html_single/)
- [Atlassian Querydsl examples](https://bitbucket.org/atlassian/querydsl-examples)
- [Querydsl google group](https://groups.google.com/forum/#!forum/querydsl)
- [Spring Data JPA documentation](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

## <a name="RunningTests"></a> Running tests

Tests require SQL Server DB.

Easies way to set it up on your machine is to use docker:

```
docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=<YourStrong!Passw0rd>' -p 1433:1433 -d microsoft/mssql-server-linux:2017-latest
```

## <a name="Contributing"></a> Contributing

If you have an idea for a new feature or want to report a bug please use the issue tracker.

Pull requests are welcome!

## <a name="License"></a> License

This library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
