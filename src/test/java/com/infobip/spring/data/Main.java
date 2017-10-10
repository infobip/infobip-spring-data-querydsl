package com.infobip.spring.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@EnableExtendedRepositories
@SpringBootApplication
public class Main {

	public static void main(String[] args) throws IOException {

		new SpringApplicationBuilder(Main.class).run(args);
	}

}
