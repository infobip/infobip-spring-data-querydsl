package com.infobip.spring.data.jpa;

import java.io.Serializable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

public class ExtendedQuerydslJpaRepositoryFactory extends JpaRepositoryFactory {

    private final JPASQLQueryFactory jpaSQLQueryFactory;
    private final EntityManager entityManager;
    private final EntityPathResolver entityPathResolver;
    private final JPAQueryFactory jpaQueryFactory;

    public ExtendedQuerydslJpaRepositoryFactory(EntityManager entityManager,
                                                JPASQLQueryFactory jpaSqlFactory,
                                                EntityPathResolver entityPathResolver,
                                                JPAQueryFactory jpaQueryFactory) {
        super(entityManager);
        this.entityManager = entityManager;
        this.jpaSQLQueryFactory = jpaSqlFactory;
        this.entityPathResolver = entityPathResolver;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

        var fragments = super.getRepositoryFragments(metadata);
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
        var path = entityPathResolver.createPath(entityInformation.getJavaType());
        var simpleJPAQuerydslFragment = getTargetRepositoryViaReflection(SimpleQuerydslJpaFragment.class,
                                                                         path,
                                                                         jpaQueryFactory,
                                                                         jpaSQLQueryFactory,
                                                                         entityManager);
        var fragment = RepositoryFragment.implemented(simpleJPAQuerydslFragment);
        return fragments.append(fragment);
    }
}
