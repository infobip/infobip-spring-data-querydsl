package com.infobip.spring.data.jpa.extension;

import com.infobip.spring.data.jpa.SimpleExtendedQuerydslJpaRepository;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.function.Supplier;

public class SimpleCustomExtendedQuerydslJpaRepository<T, ID extends Serializable> extends SimpleExtendedQuerydslJpaRepository<T, ID> {

    public SimpleCustomExtendedQuerydslJpaRepository(JpaEntityInformation<T, ID> entityInformation,
                                                     EntityManager entityManager,
                                                     Supplier<JPASQLQuery<T>> jpaSqlFactory) {
        super(entityInformation, entityManager, jpaSqlFactory);
    }
}
