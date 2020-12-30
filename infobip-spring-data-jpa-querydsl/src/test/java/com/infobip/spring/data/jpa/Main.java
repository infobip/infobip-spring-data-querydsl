package com.infobip.spring.data.jpa;

import com.infobip.spring.data.jpa.extension.EnableCustomExtendedJpaRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@EnableCustomExtendedJpaRepositories
@EnableExtendedJpaRepositories
@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		new SpringApplicationBuilder(Main.class).run(args);
	}

}
