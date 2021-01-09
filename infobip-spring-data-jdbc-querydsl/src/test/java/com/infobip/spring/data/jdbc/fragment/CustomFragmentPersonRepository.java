package com.infobip.spring.data.jdbc.fragment;

import com.infobip.spring.data.jdbc.Person;
import com.infobip.spring.data.jdbc.QuerydslJdbcFragment;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomFragmentPersonRepository
        extends PagingAndSortingRepository<Person, Long>, QuerydslPredicateExecutor<Person>, QuerydslJdbcFragment<Person> {
}
