package jpa;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;

interface PersonRepository extends ExtendedQuerydslJpaRepository<Person, Long> {
}
