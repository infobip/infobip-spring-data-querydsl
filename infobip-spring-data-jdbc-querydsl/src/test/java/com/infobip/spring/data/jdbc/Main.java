package com.infobip.spring.data.jdbc;

import com.infobip.spring.data.jdbc.extension.EnableCustomQuerydslJdbcRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableCustomQuerydslJdbcRepositories
@EnableQuerydslJdbcRepositories
@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		new SpringApplicationBuilder(Main.class).run(args);
	}

}
