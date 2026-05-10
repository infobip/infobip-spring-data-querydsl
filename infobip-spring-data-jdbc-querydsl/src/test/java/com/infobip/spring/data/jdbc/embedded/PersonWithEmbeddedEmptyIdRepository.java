package com.infobip.spring.data.jdbc.embedded;

import java.util.List;

import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonWithEmbeddedEmptyIdRepository
        extends QuerydslJdbcRepository<PersonWithEmbeddedEmptyId, FirstAndLastName> {

    List<PersonWithEmbeddedEmptyId> findByIdFirstName(String firstName);
}
