package com.infobip.spring.data.jpa.extension;

import com.infobip.spring.data.jpa.ExtendedQuerydslRepositoryJpa;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomExtendedQuerydslRepositoryJpa<T, ID> extends ExtendedQuerydslRepositoryJpa<T, ID> {
}
