package com.infobip.spring.data.jdbc.extension;

import com.infobip.spring.data.jdbc.Person;

public interface CustomExtensionPersonRepository extends CustomQuerydslJdbcRepository<Person, Long> {
}
