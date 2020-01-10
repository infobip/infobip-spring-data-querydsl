package com.infobip.spring.data.jdbc.person.settings;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

@Value
public class PersonSettings {

	@With
	@Id
	private final Long id;
	private final Long personId;
}
