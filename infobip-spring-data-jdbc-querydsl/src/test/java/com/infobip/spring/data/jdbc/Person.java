package com.infobip.spring.data.jdbc;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Value
public class Person {

	@With
	@Id
	private final Long id;
	private final String firstName;
	private final String lastName;
	private final Instant createdAt;
}
