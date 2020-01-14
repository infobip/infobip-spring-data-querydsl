package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.jpa.LicenseAcceptedMSSQLServerContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(PER_CLASS)
@SpringBootTest(classes = Main.class)
@ContextConfiguration(initializers = LicenseAcceptedMSSQLServerContainer.class)
public abstract class TestBase {

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @AfterEach
    public void clearRepositories() {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
