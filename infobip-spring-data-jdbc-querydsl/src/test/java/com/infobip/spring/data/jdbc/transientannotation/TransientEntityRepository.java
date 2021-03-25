package com.infobip.spring.data.jdbc.transientannotation;

import com.infobip.spring.data.jdbc.NoArgsEntity;
import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface TransientEntityRepository extends QuerydslJdbcRepository<TransientEntity, Long> {
}
