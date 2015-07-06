package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Function;

import java.sql.ResultSet;

/**
 * Created by ruslan on 06.07.2015.
 */
public class SqlServerFunction extends SqlServerRoutine implements Function {
    public static final String SELECT_FUNCTIONS_QUERY = "select ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_BODY from information_schema.routines " +
            "where routine_type = 'FUNCTION'";

    public SqlServerFunction(ResultSet rs, String url, SqlServerDatabase parent) {
        super(rs, url, parent);
    }

    @Override
    public String toString() {
        return "SqlServerFunction{" +
                "name='" + getName() + '\'' +
                ", schema='" + getSchema() + '\'' +
                '}';
    }
}
