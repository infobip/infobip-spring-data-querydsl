package com.infobip.spring.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Optional;

import com.querydsl.sql.types.Type;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

public class AggregateReferenceType implements Type<AggregateReference> {

    @Override
    public int[] getSQLTypes() {
        return new int[]{ Types.BIGINT };
    }

    @Override
    public String getLiteral(AggregateReference value) {
        return Optional.ofNullable(value).map(AggregateReference::getId).map(Object::toString).orElseGet(null);
    }

    @Override
    public Class<AggregateReference> getReturnedClass() {
        return AggregateReference.class;
    }

    @Override
    public AggregateReference getValue(ResultSet rs, int startIndex) throws SQLException {
        return AggregateReference.to(rs.getObject(startIndex));
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, AggregateReference value) throws SQLException {
        st.setObject(startIndex, value.getId());
    }

}
