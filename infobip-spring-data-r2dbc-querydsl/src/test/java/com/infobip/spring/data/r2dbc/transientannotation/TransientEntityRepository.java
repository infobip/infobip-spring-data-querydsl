package com.infobip.spring.data.r2dbc.transientannotation;

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository;

public interface TransientEntityRepository extends QuerydslR2dbcRepository<TransientEntity, Long> {
}
