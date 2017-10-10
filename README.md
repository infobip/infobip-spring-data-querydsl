# Infobip Spring Data JPA Querydsl

[![Build Status](https://travis-ci.org/infobip/infobip-spring-data-jpa-querydsl.svg?branch=master)](https://travis-ci.org/infobip/infobip-spring-data-jpa-querydsl)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-jpa-querydsl-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.infobip/infobip-spring-data-jpa-querydsl-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/infobip/infobip-spring-data-jpa-querydsl/badge.svg?branch=master)](https://coveralls.io/github/infobip/infobip-spring-data-jpa-querydsl?branch=master)

Infobip Spring Data JPA Querydsl provides new functionality that enables the user to leverage the full power of Querydsl API on top of Spring Data repository infrastructure.

## Contents

1. [Features and examples](#FeaturesAndExamples)
    * [Native queries with Querydsl](#NativeQueriesWithQuerydsl)
    * [Projections](#Projections)
    * [Query](#Query)
    * [Update](#Update)
    * [Delete](#Delete)
    * [List instead of Iterable return type](#ListInsteadOfIterableReturnType)
    * [Transactional support](#TransactionalSupport)
    * [Optional](#Optional)
2. [Setup](#Setup)
3. [Requirements](#Requirements)
4. [Contributing](#Contributing)

## <a name="FeaturesAndExamples"></a> Features and examples:

### <a name="NativeQueriesWithQuerydsl"></a> Native queries with Querydsl:

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

### <a name="Projections"></a> Projections

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

### <a name="Query"></a> Query

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

### <a name="Update"></a> Update

```
repository.update(query -> query
          .set(person.firstName, "John")
          .where(person.firstName.eq("Johny"))
          .execute());
```

### <a name="Delete"></a> Delete

```
long numberOfAffectedRows = repository.deleteWhere(person.firstName.like("John%"));
```

### <a name="ListInsteadOfIterableReturnType"></a> List instead of Iterable return type

[QueryDslPredicateExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html)#findAll methods return Iterable which can be cumbersome to use. 
Those methods were overridden and now return a List which is easier to use and is easier to convert to Stream.

### <a name="TransactionalSupport"></a> Transactional support

Query execution is always done inside the repository implementation (loan pattern) in a transaction so transactions don't have to be 
handled manually (like they do if you are manually managing JPAQuery and other Querydsl constructs).

### <a name="Optional"></a> Optional

Methods which can return null (like findOne(id) or findOne(Predicate)) have been deprecated and replaced with alternative methods that return Optional:

```
Optional<Person> actual = repository.findOneById(johnDoe.getId());
Optional<Person> actual = repository.findOneByPredicate(person.firstName.eq("John"));
```

## <a name="Setup"></a> Setup:

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

2. Add @EnableExtendedRepositories to your Main class:

```java
@EnableExtendedRepositories // replaces @EnableJpaRepositories
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

## <a name="Requirements"></a> Requirements:

- Java 8
- Hibernate (if you need support for other JPA implementors please open an issue)
- Spring Data
- Querydsl

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