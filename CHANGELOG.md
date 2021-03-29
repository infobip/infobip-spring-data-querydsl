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
