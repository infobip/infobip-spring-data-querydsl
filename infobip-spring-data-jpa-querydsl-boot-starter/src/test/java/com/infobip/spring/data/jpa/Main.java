package com.infobip.spring.data.jpa;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		new SpringApplicationBuilder(Main.class).run(args);
	}
}
