package com.infobip.spring.data.jdbc.embedded;

import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonWithEmbeddedFirstAndLastNameRepository
        extends QuerydslJdbcRepository<PersonWithEmbeddedFirstAndLastName, Long> {

}
