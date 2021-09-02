package com.infobip.spring.data.jpa.embedded;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "Person")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class PersonWithEmbeddedFirstAndLastName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Embedded
    private FirstAndLastName firstAndLastName;
}

