package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Column;
import io.arusland.dbinfo.ColumnType;
import io.arusland.dbinfo.Restriction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ruslan on 05.07.2015.
 */
public class SqlServerColumn implements Column {
    public final static String SELECT_COLUMNS_QUERY = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = N'%s' and TABLE_SCHEMA = N'%s' " +
            "ORDER BY ORDINAL_POSITION";
    private final String url;
    private final SqlServerTable parent;
    private final SqlServerColumnType type;
    private final String name;
    private final boolean isNullable;

    public SqlServerColumn(ResultSet rs, String url, SqlServerTable parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("COLUMN_NAME"));
            this.type = new SqlServerColumnType(rs.getString("DATA_TYPE"), getNullableInt(rs.getString("CHARACTER_MAXIMUM_LENGTH")),
                    getNullableInt(rs.getString("NUMERIC_PRECISION")), getNullableInt(rs.getString("NUMERIC_SCALE")));
            this.isNullable = "YES".equals(rs.getString("IS_NULLABLE"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    private static Integer getNullableInt(String value) {
        if (!StringUtils.isBlank(value)){
            return Integer.parseInt(value);
        }

        return null;
    }

    @Override
    public String toString() {
        return "SqlServerColumn{" +
                "name=" + name +
                ", type='" + type + '\'' +
                ", isNullable=" + isNullable +
                '}';
    }
}
