package com.infobip.spring.data.jdbc;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

@Value
class PersonSettings {

	@With
	@Id
	private final Long id;
	private final Long personId;
}
