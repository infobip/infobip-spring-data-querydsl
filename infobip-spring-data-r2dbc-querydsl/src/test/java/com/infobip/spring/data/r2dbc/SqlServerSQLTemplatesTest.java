package com.infobip.spring.data.r2dbc;

import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLTemplates;
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
