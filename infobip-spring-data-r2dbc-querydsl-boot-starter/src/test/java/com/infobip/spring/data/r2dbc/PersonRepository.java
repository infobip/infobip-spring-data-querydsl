package com.infobip.spring.data.r2dbc;

interface PersonRepository extends QuerydslR2dbcRepository<Person, Long> {
}
