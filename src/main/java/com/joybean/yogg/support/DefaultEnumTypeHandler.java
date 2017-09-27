package com.joybean.yogg.support;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author jobean
 */
@MappedJdbcTypes(value = JdbcType.TINYINT, includeNullJdbcType = true)
public class DefaultEnumTypeHandler extends BaseTypeHandler<EnumValue> {
    private Class<EnumValue> type;

    public DefaultEnumTypeHandler() {
    }

    public DefaultEnumTypeHandler(Class<EnumValue> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, EnumValue enumValue, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, enumValue.getValue());
    }

    @Override
    public EnumValue getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return EnumValue.convert(resultSet.getInt(s), type);
    }

    @Override
    public EnumValue getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return EnumValue.convert(resultSet.getInt(i), type);
    }

    @Override
    public EnumValue getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return EnumValue.convert(callableStatement.getInt(i), type);
    }

}
