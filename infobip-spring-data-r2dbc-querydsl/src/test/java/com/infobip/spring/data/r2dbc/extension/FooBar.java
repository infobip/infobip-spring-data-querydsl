package com.infobip.spring.data.r2dbc.extension;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

@Value
public class FooBar {

	@With
	@Id
	private final Long id;
}
