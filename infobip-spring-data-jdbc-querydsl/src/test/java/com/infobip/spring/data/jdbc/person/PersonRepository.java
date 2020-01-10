package com.infobip.spring.data.jdbc.person;

import com.infobip.spring.data.jdbc.QPerson;
import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonRepository extends QuerydslJdbcRepository<Person, QPerson, Long> {
}