package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Procedure;
import org.apache.commons.lang3.Validate;

import java.sql.*;

/**
 * Created by ruslan on 06.07.2015.
 */
public class SqlServerProcedure implements Procedure {
    public static final String SELECT_PROCEDURES_QUERY = "select ROUTINE_SCHEMA, ROUTINE_NAME from information_schema.routines " +
            "where routine_type = 'PROCEDURE'";
    private static final String SELECT_PROCEDURE_QUERY = "{call sp_helptext(?)}";
    private final String url;
    private final SqlServerDatabase parent;
    private final String name;
    private final String schema;
    private String text;

    public SqlServerProcedure(ResultSet rs, String url, SqlServerDatabase parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("ROUTINE_NAME"));
            this.schema = Validate.notBlank(rs.getString("ROUTINE_SCHEMA"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContent() {
        if (text == null){
            try (Connection con = DriverManager.getConnection(url)) {
                con.setCatalog(parent.getName());
                text = getProcedureText(con);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return text;
    }

    private String getProcedureText(Connection con) throws SQLException {
        CallableStatement  stmt = con.prepareCall(SELECT_PROCEDURE_QUERY);
        stmt.setString(1, String.format("[%s].[%s]", schema, name));
        ResultSet rs = stmt.executeQuery();
        StringBuilder result = new StringBuilder();

        while (rs.next()) {
            result.append(rs.getString("Text"));
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return "SqlServerProcedure{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
