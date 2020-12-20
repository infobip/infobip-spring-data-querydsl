package com.infobip.spring.data.jpa;

import com.infobip.spring.data.common.InfobipSpringDataCommonConfiguration;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@Import(InfobipSpringDataCommonConfiguration.class)
@Configuration
public class ExtendedQuerydslJpaConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public Supplier<JPASQLQuery<?>> jpaSqlFactory(EntityManager entityManager, SQLTemplates sqlTemplates) {
        return () -> new JPASQLQuery<>(entityManager, sqlTemplates);
    }
}
