package com.infobip.spring.data.jpa;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.*;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.MSSQLServerContainer;

import static org.testcontainers.containers.MSSQLServerContainer.MS_SQL_SERVER_PORT;

public class LicenseAcceptedMSSQLServerContainer
        implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<ContextClosedEvent> {

    private static final MSSQLServerContainer INSTANCE = new MSSQLServerContainer() {
        @Override
        protected void configure() {
            addExposedPort(MS_SQL_SERVER_PORT);
            addEnv("ACCEPT_EULA", "Y");
            addEnv("SA_PASSWORD", getPassword());
        }
    };

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        INSTANCE.start();
        String url = applicationContext.getEnvironment()
                                       .getProperty("spring.datasource.url")
                                       .replace("<port>", INSTANCE.getMappedPort(MS_SQL_SERVER_PORT).toString());
        TestPropertyValues values = TestPropertyValues.of(
                "spring.datasource.url=" + url);
        values.applyTo(applicationContext);
        DatabaseCreator creator = new DatabaseCreator(url, INSTANCE.getUsername(), INSTANCE.getPassword());
        creator.createDatabaseIfItDoesntExist();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        INSTANCE.stop();
    }
}