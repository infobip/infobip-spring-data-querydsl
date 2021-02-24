package com.infobip.spring.data.jpa;

import com.querydsl.jpa.sql.JPASQLQuery;

public interface JPASQLQueryFactory {

    JPASQLQuery<?> create();
}
