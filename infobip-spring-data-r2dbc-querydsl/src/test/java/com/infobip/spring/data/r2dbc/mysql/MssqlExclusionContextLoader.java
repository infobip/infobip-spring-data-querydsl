package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.testcontainers.spring.mssql.MSSQLServerContainerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.test.context.MergedContextConfiguration;

import java.util.List;

public class MssqlExclusionContextLoader extends SpringBootContextLoader {

    @Override
    protected List<ApplicationContextInitializer<?>> getInitializers(MergedContextConfiguration config,
                                                                     SpringApplication application) {
        var initializers = super.getInitializers(config, application)
                                .stream()
                                .filter(initializer -> !(initializer instanceof MSSQLServerContainerInitializer))
                                .toList();
        return initializers;
    }
}
