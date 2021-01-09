package com.infobip.spring.data.jpa.extension;

import com.infobip.spring.data.jpa.Person;

public interface CustomExtensionPersonRepository extends CustomExtendedQuerydslJpaRepository<Person, Long> {
}
