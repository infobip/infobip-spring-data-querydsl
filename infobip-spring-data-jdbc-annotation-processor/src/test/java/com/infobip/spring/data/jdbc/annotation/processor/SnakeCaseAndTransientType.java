package com.infobip.spring.data.jdbc.annotation.processor;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("customer_order")
@Schema("dbo")
@Data
@AllArgsConstructor
public class SnakeCaseAndTransientType {

    @Id
    @Column("id")
    private Long id;

    @Column("customer_id")
    private Long customerId;

    @Transient
    private List<Long> orderItemIds;
}
