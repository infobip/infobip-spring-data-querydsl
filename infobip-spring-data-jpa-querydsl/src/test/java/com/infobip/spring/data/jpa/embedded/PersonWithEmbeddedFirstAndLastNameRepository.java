package com.infobip.spring.data.jpa.embedded;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;

public interface PersonWithEmbeddedFirstAndLastNameRepository
        extends ExtendedQuerydslJpaRepository<PersonWithEmbeddedFirstAndLastName, Long> {

}
