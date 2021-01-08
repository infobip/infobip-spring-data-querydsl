package com.infobip.spring.data.r2dbc.extension;

public interface FooBarRepository extends CustomQuerydslR2dbcRepository<FooBar, Long> {
}
