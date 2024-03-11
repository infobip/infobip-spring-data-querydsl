package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.*;

@ContextConfiguration(loader = MssqlExclusionContextLoader.class)
@ActiveProfiles("mysql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MysqlNamedParametersSupportTest extends TestBase {

    @Test
    void shouldNotFail() {
        // given
        // when
        // then
    }
}
