package com.infobip.spring.data.jdbc;

interface PersonRepository extends QuerydslJdbcRepository<Person, Long> {
}