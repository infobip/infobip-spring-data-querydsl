package com.infobip.spring.data.jdbc.embedded;

import java.util.List;
import java.util.Optional;

import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonWithEmbeddedIdRepository
        extends QuerydslJdbcRepository<PersonWithEmbeddedId, FirstAndLastName> {

    List<PersonWithEmbeddedId> findByIdFirstName(String firstName);

    List<PersonWithEmbeddedId> findByIdLastName(String lastName);

    Optional<PersonWithEmbeddedId> findByIdFirstNameAndIdLastName(String firstName, String lastName);
}
