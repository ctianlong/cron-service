package com.system.cron.util.mybatis.typehandler;

import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by ctianlong on 2018/5/28.
 */
//@MappedJdbcTypes(JdbcType.BIGINT)
//@MappedTypes(Date.class)
public class BigintDateTypeHandler extends BaseTypeHandler<Date>
{
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter,
        JdbcType jdbcType)
        throws SQLException
    {
        ps.setLong(i, parameter.getTime());
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName)
        throws SQLException
    {
        return new Date(rs.getLong(columnName));
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex)
        throws SQLException
    {
        return new Date(rs.getLong(columnIndex));
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex)
        throws SQLException
    {
        return new Date(cs.getLong(columnIndex));
    }
}
