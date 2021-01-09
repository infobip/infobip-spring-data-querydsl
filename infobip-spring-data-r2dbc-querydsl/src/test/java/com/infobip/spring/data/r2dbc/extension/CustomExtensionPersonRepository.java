package com.infobip.spring.data.r2dbc.extension;

import com.infobip.spring.data.r2dbc.Person;

public interface CustomExtensionPersonRepository extends CustomQuerydslR2dbcRepository<Person, Long> {
}
