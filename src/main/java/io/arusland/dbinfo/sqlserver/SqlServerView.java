package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.View;
import io.arusland.dbinfo.util.DbUtil;
import org.apache.commons.lang3.Validate;

import java.sql.*;
import java.util.Date;

/**
 * Created by ruslan on 07.07.2015.
 */
public class SqlServerView implements View {
    public final static String SELECT_VIEWS_QUERY = "SELECT ss.name as schema_name, v.object_id, v.name, v.type_desc, v.create_date, v.modify_date FROM sys.views v " +
            "inner join sys.schemas ss on ss.schema_id = v.schema_id";
    private final static String SELECT_VIEW_CONTENT_QUERY = "select definition from sys.sql_modules where object_id = %d";
    private final String url;
    private final SqlServerDatabase parent;
    private final String name;
    private final String schema;
    private final Date createDate;
    private final Date modifyDate;
    private final long objectId;
    private String content;

    public SqlServerView(ResultSet rs, String url, SqlServerDatabase parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("name"));
            this.schema = Validate.notBlank(rs.getString("schema_name"));
            this.createDate = DbUtil.getDate(rs, "create_date");
            this.modifyDate = DbUtil.getDate(rs, "modify_date");
            this.objectId = rs.getLong("object_id");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public String getContent() {
        if (content == null){
            try (Connection con = DriverManager.getConnection(url)) {
                con.setCatalog(parent.getName());
                content = getContent(con);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return content;
    }

    private String getContent(final Connection con) throws SQLException {
        final Statement stmt = con.createStatement();
        final ResultSet rs = stmt.executeQuery(String.format(SELECT_VIEW_CONTENT_QUERY, objectId));

        if (rs.next()) {
            return rs.getString("definition");
        }

        return null;
    }

    @Override
    public String toString() {
        return "SqlServerView{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                '}';
    }
}
