package com.infobip.spring.data.jdbc.embedded;

import java.util.List;

import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonWithEmbeddedNullableIdRepository
        extends QuerydslJdbcRepository<PersonWithEmbeddedNullableId, FirstAndLastName> {

    List<PersonWithEmbeddedNullableId> findByIdFirstName(String firstName);
}
