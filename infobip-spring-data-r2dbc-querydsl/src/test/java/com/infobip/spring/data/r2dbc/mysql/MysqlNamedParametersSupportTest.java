package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.EnableQuerydslR2dbcRepositories;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.*;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ContextConfiguration(loader = MssqlExclusionContextLoader.class)
@ActiveProfiles("mysql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(PER_CLASS)
@SpringBootTest(classes = MysqlNamedParametersSupportTest.Main.class)
public class MysqlNamedParametersSupportTest {

    @Test
    void shouldNotFail() {
        // given
        // when
        // then
    }

    @EnableQuerydslR2dbcRepositories
    @SpringBootApplication
    public static class Main {

        public static void main(String[] args) {

            new SpringApplicationBuilder(com.infobip.spring.data.r2dbc.Main.class).run(args);
        }

        @Configuration
        public static class TestConfiguration {

            @Bean
            public SQLTemplates sqlTemplates() {
                return new MySQLTemplates();
            }
        }
    }
}
