/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infobip.spring.data.jpa;

import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.function.Supplier;

public class ExtendedQuerydslJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    private EntityManager entityManager;
    private EntityPathResolver entityPathResolver;
    private EscapeCharacter escapeCharacter;
    private JpaQueryMethodFactory queryMethodFactory;
    private Supplier<JPASQLQuery<?>> jpaSqlFactory;

    public ExtendedQuerydslJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        this.entityManager = entityManager;
    }

    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        super.setEntityPathResolver(resolver);
        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    @Autowired
    public void setQueryMethodFactory(@Nullable JpaQueryMethodFactory factory) {
        super.setQueryMethodFactory(factory);
        if (factory != null) {
            this.queryMethodFactory = factory;
        }
    }

    @Autowired
    public void setJpaSqlFactory(Supplier<JPASQLQuery<?>> jpaSqlFactory) {
        this.jpaSqlFactory = jpaSqlFactory;
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        ExtendedQuerydslJpaRepositoryFactory factory = new ExtendedQuerydslJpaRepositoryFactory(entityManager,
                                                                                                jpaSqlFactory);
        factory.setEntityPathResolver(this.entityPathResolver);
        factory.setEscapeCharacter(this.escapeCharacter);
        if (this.queryMethodFactory != null) {
            factory.setQueryMethodFactory(this.queryMethodFactory);
        }
        factory.setRepositoryBaseClass(SimpleExtendedQuerydslJpaRepository.class);

        return factory;
    }

    public void setEscapeCharacter(char escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
    }
}
