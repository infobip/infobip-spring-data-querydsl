package com.infobip.spring.data.r2dbc.mysql;

import com.infobip.spring.data.r2dbc.EnableQuerydslR2dbcRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableQuerydslR2dbcRepositories
@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		new SpringApplicationBuilder(Main.class).run(args);
	}

}
