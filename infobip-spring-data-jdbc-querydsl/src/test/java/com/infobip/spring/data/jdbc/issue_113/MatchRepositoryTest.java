package com.infobip.spring.data.jdbc.issue_113;

import com.infobip.spring.data.jdbc.TestBase;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

@AllArgsConstructor
public class MatchRepositoryTest extends TestBase {

    private final MatchRepository repository;

    @Test
    void shouldQueryMany() {
        // given
        var givenMatch = new Match(null, new Player(1, "one"), new Player(2, "two"));
        repository.save(givenMatch);

        // when
        var actual = repository.queryMany(q -> q.select(repository.entityProjection()).from(QMatch.match));
//        var actual = repository.findAll();

        // then
        then(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("matchId")
                 .containsExactly(givenMatch);
    }
}
