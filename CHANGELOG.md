### 7.2.1

* added support for `Table#name` [#72](/../../issues/72)

### 7.2.0
* added basic support for `@MappedCollections`
* changed order of columns in generated Q class to match the field order in source class

### 7.1.0
* added generatedAnnotationClass support to annotation processor

### 7.0.0

* upgraded to Spring Boot 2.7.0 (not backwards compatible with previous versions of Spring Data)

### 6.2.2

* removed all `@Transactional(readOnly = true)` from interfaces and added them on concrete classes [#60](/../../issues/60)
* upgraded to Spring Boot 2.6.7

### 6.2.1

* fixed jdbc module not reusing connections [#52](/../../issues/52)
* added spring sql exception translation

### 6.2.0

* upgraded to Spring Boot 2.6.1
* added `streamAll` to JDBC and JPA modules

### 6.1.2
* fixed sorting issue [#39](/../../issues/39)

### 6.1.1
* add support for Kotlin data classes without PersistenceConstructor [#37](/../../issues/37)

### 6.1.0
* added support for `@Embedded` to JDBC module

### 6.0.0
* upgraded to Querydsl 5.0.0
* upgraded to Spring Boot 2.5.1
* fixed null handling in QuerydslJdbcPredicateExecutor#queryOne

### 5.4.2
* undeprecated `QuerydslJdbcFragment#query` - added warning in javadoc about https://github.com/spring-projects/spring-data-jdbc/issues/986

### 5.4.1
* failed release due to sonatype repository misconfiguration

### 5.4.0
* added `@ProjectTableCaseFormat` and `@ProjectColumnCaseFormat` for easier control over generated table and 
  column names case format in annotation processor

### 5.3.0
* added infobip-spring-data-jdbc-annotation-processor-common for custom annotation processor naming strategies (#21)

### 5.2.0
* added support for repositories that don't implement `QuerydslJdbcFragment` (JDBC module) and `QuerydslR2dbcFragment` (R2DBC module)

### 5.1.0
* added support for `@Transient` to JDBC and R2DBC module (#18)

### 5.0.5
* added support for `@Transient` to annotation processor (#18)

### 5.0.4
* deprecated `QuerydslJdbcFragment#query` (replaced by new methods)
* added `QuerydslJdbcFragment#queryOne` and `QuerydslJdbcFragment#queryMany`

### 5.0.2 - 5.0.3
* problems with new CI release build (no changes in codebase)

### 5.0.1

* Fixed an issue with colliding beans of `java.util.Supplier` type when using JPA module
* **Breaking change**:
    * if you're using JPA module and have been overriding `jpaSqlFactory` bean, you need to change bean definition to `JPASQLQueryFactory jpaSqlQueryFactory`

### 5.0.0

* R2DBC support
* Spring Boot starters for modules
* fragment support
* QuerydslPredicateExecutor fragment support to JDBC module (JPA already has it and R2DBC module has ReactiveQuerydslPredicateExecutor support)
* **Breaking change**:
    * broke up repositories into fragments to enable easier extension and maintenance (most users shouldn't be affected)

### 4.1.2

* JPA and JDBC module extension support

### 4.1.1

* support for multiple constructors in JDBC module

### 4.1.0

* `infobip-spring-data-jpa-querydsl` is no longer coupled to Hibernate ORM
* `SQLTemplates` for `infobip-spring-data-jpa-querydsl` can now be overidden - simply provide a bean of type SQLTemplates in your context.
* `QuerydslJdbcRepository` now extends `PagingAndSortingRepository`

### 4.0.0

* Breaking change:
  * removed second generic parameter from QuerydslJdbcRepository

### 3.0.0

* Breaking changes:
  * renamed `@EnableExtendedRepositories` to `@EnableExtendedJpaRepositories`
  * renamed `ExtendedQueryDslJpaRepository` to `ExtendedQuerydslJpaRepository`
* Added new module - infobip-spring-data-jdbc-querydsl.

### 2.0.1

Changed scope of SimpleExtendedQueryDslJpaRepository to public.


### 2.0.0

Upgrade to Spring Data 2 (Spring Boot 2).

Breaking change: new repository methods introduced in Spring Data 2 `CrudRepository#findById(Object)` and
`QuerydslPredicateExecutor#findOne(Predicate)` replace old `ExtendedQueryDslJpaRepository.findOneById(Object)`
and `ExtendedQueryDslJpaRepository.findOneByPredicate(Predicate)` (they were removed).
