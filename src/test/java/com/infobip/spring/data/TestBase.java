package com.infobip.spring.data;

import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.MSSQLServerContainer;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
@ContextConfiguration(initializers = LicenseAcceptedMSSQLServerContainer.class)
public abstract class TestBase {

    @Autowired
    private List<JpaRepository<?, ?>> repositories;

    @After
    public void clearRepositories() throws Exception {

        repositories.forEach(JpaRepository::deleteAllInBatch);
    }
}
