package com.infobip.spring.data.jpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.function.Supplier;

public class ExtendedQuerydslJpaRepositoryFactory extends JpaRepositoryFactory {

    private final Supplier<JPASQLQuery<?>> jpaSqlFactory;
    private final EntityManager entityManager;
    private final EntityPathResolver entityPathResolver;
    private final JPAQueryFactory jpaQueryFactory;

    ExtendedQuerydslJpaRepositoryFactory(EntityManager entityManager,
                                         Supplier<JPASQLQuery<?>> jpaSqlFactory,
                                         EntityPathResolver entityPathResolver,
                                         JPAQueryFactory jpaQueryFactory) {
        super(entityManager);
        this.entityManager = entityManager;
        this.jpaSqlFactory = jpaSqlFactory;
        this.entityPathResolver = entityPathResolver;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
        EntityPath<?> path = entityPathResolver.createPath(entityInformation.getJavaType());
        Object simpleJPAQuerydslFragment = getTargetRepositoryViaReflection(SimpleQuerydslJpaFragment.class,
                                                                            path,
                                                                            jpaQueryFactory,
                                                                            jpaSqlFactory,
                                                                            entityManager);
        RepositoryFragment<Object> fragment = RepositoryFragment.implemented(simpleJPAQuerydslFragment);
        return fragments.append(fragment);
    }
}
