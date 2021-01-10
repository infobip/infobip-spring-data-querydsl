package com.infobip.spring.data.r2dbc.fragment;

import com.infobip.spring.data.r2dbc.Person;
import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface CustomFragmentPersonRepository
        extends ReactiveSortingRepository<Person, Long>, ReactiveQuerydslPredicateExecutor<Person>, QuerydslR2dbcFragment<Person> {
}
