package com.infobip.spring.data.jpa;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
class PersonSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column
	private Long personId;

	PersonSettings(Long personId) {
		this.personId = personId;
	}
}
