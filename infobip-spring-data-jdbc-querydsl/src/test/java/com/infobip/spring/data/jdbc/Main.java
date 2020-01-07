package com.infobip.spring.data.jdbc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableQuerydslJdbcRepositories
@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		new SpringApplicationBuilder(Main.class).run(args);
	}

}
