package com.infobip.spring.data.jdbc.issue_113;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_NULL;

@Table("matches")
public record Match(
        @Id
        Integer matchId,
        @Embedded(onEmpty = USE_NULL, prefix = "player1_")
        Player playerOne,
        @Embedded(onEmpty = USE_NULL, prefix = "player2_")
        Player playerTwo
) {}
