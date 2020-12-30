package com.infobip.spring.data.jdbc.extension;

import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomQuerydslJdbcRepository<T, ID> extends QuerydslJdbcRepository<T, ID> {
}
