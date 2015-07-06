package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Procedure;

import java.sql.*;

/**
 * Created by ruslan on 06.07.2015.
 */
public class SqlServerProcedure extends SqlServerRoutine implements Procedure {
    public static final String SELECT_PROCEDURES_QUERY = "select ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_BODY from information_schema.routines " +
            "where routine_type = 'PROCEDURE'";

    public SqlServerProcedure(ResultSet rs, String url, SqlServerDatabase parent) {
        super(rs, url, parent);
    }

    @Override
    public String toString() {
        return "SqlServerProcedure{" +
                "name='" + getName() + '\'' +
                ", schema='" + getSchema() + '\'' +
                '}';
    }
}
