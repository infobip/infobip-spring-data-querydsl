package com.infobip.spring.data.jpa;

import com.infobip.spring.data.common.InfobipSpringDataCommonConfiguration;
import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(InfobipSpringDataCommonConfiguration.class)
@Configuration
public class ExtendedQuerydslJpaConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public JPASQLQueryFactory jpaSqlQueryFactory(EntityManager entityManager, SQLTemplates sqlTemplates) {
        return () -> new JPASQLQuery<>(entityManager, sqlTemplates);
    }

    @ConditionalOnMissingBean
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(JPAProvider.getTemplates(entityManager), entityManager);
    }
}
