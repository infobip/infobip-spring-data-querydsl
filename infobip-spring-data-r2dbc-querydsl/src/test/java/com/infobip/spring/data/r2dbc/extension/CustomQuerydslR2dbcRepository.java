package com.infobip.spring.data.r2dbc.extension;

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomQuerydslR2dbcRepository<T, ID> extends QuerydslR2dbcRepository<T, ID> {
}
