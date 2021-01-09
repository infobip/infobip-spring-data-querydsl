package com.infobip.spring.data.jpa.fragment;

import com.infobip.spring.data.jpa.Person;
import com.infobip.spring.data.jpa.QuerydslJpaFragment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CustomJpaFragmentPersonRepository
        extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person>, QuerydslJpaFragment<Person> {
}
