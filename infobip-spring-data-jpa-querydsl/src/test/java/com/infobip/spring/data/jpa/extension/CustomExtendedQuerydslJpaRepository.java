package com.infobip.spring.data.jpa.extension;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomExtendedQuerydslJpaRepository<T, ID> extends ExtendedQuerydslJpaRepository<T, ID> {
}
