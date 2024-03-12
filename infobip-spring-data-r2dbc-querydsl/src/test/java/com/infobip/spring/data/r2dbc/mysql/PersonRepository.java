package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository;
import org.springframework.context.annotation.Profile;

@Profile("mysql")
public interface PersonRepository extends QuerydslR2dbcRepository<Person, Long> {
}
