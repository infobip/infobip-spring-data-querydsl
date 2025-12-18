package com.infobip.spring.data.jdbc.annotation.processor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

class Nest {
    @Schema("dbo")
    @Table("foo")
    public static class NestedFoo {

        @Id
        @Column("id")
        private final Long id;

        public NestedFoo(Long id) {
            this.id = id;
        }
    }
}
