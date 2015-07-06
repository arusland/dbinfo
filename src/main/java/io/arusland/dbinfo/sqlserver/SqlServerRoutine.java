package io.arusland.dbinfo.sqlserver;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.sql.*;

/**
 * Created by ruslan on 06.07.2015.
 */
public abstract class SqlServerRoutine {
    private static final String CALL_SP_HELPTEXT = "{call sp_helptext(?)}";
    private final String url;
    private final SqlServerDatabase parent;
    private final String name;
    private final String schema;
    private final boolean isSqlBody;
    private String content;

    protected SqlServerRoutine(ResultSet rs, String url, SqlServerDatabase parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("ROUTINE_NAME"));
            this.schema = Validate.notBlank(rs.getString("ROUTINE_SCHEMA"));
            this.isSqlBody = "SQL".equals(rs.getString("ROUTINE_BODY"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getContent() {
        if (content == null) {
            if (isSqlBody) {
                try (Connection con = DriverManager.getConnection(getUrl())) {
                    con.setCatalog(getParent().getName());
                    content = getRoutineContent(con);
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                content = StringUtils.EMPTY;
            }

        }

        return content;
    }

    protected String getRoutineContent(Connection con) throws SQLException {
        CallableStatement stmt = con.prepareCall(CALL_SP_HELPTEXT);
        stmt.setString(1, String.format("[%s].[%s]", schema, name));
        ResultSet rs = stmt.executeQuery();
        StringBuilder result = new StringBuilder();

        while (rs.next()) {
            result.append(rs.getString("Text"));
        }

        return result.toString();
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    protected SqlServerDatabase getParent() {
        return parent;
    }

    protected String getUrl() {
        return url;
    }
}
