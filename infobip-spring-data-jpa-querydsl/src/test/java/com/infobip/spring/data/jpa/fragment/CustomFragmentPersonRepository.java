package com.infobip.spring.data.jpa.fragment;

import com.infobip.spring.data.jpa.JPAQuerydslFragment;
import com.infobip.spring.data.jpa.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CustomFragmentPersonRepository extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person>, JPAQuerydslFragment<Person> {
}
