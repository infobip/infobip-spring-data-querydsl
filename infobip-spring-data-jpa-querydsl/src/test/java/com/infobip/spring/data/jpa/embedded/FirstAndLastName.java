package com.infobip.spring.data.jpa.embedded;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
public class FirstAndLastName {

    private String firstName;
    private String lastName;
}
