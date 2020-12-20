package com.infobip.spring.data.jdbc;

import com.querydsl.sql.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class SqlServerSQLTemplatesTest extends TestBase {

    private final SQLTemplates sqlTemplates;

    @Test
    void shouldUseSQLServer2012Templates() {
        // then
        then(sqlTemplates).isInstanceOf(SQLServer2012Templates.class);
    }
}
