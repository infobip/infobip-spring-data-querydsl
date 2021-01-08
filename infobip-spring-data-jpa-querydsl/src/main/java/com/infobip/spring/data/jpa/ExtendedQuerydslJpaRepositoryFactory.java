package com.infobip.spring.data.jpa;

import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.function.Supplier;

public class ExtendedQuerydslJpaRepositoryFactory extends JpaRepositoryFactory {

    private final Supplier<JPASQLQuery<?>> jpaSqlFactory;

    ExtendedQuerydslJpaRepositoryFactory(EntityManager entityManager,
                                         Supplier<JPASQLQuery<?>> jpaSqlFactory) {
        super(entityManager);
        this.jpaSqlFactory = jpaSqlFactory;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                    EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        Object repository = getTargetRepositoryViaReflection(information, entityInformation, entityManager,
                                                             jpaSqlFactory);

        Assert.isInstanceOf(JpaRepositoryImplementation.class, repository);

        return (JpaRepositoryImplementation<?, ?>) repository;
    }
}
