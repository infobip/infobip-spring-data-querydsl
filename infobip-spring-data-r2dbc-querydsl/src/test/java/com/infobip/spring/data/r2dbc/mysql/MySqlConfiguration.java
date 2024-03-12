package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.TestConfiguration;
import org.springframework.context.annotation.*;

@Import(TestConfiguration.class)
@Profile("mysql")
@Configuration
public class MySqlConfiguration {
}
