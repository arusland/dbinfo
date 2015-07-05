package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Column;
import io.arusland.dbinfo.Table;
import io.arusland.dbinfo.util.ResultSetUtil;
import org.apache.commons.lang3.Validate;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ruslan on 05.07.2015.
 */
public class SqlServerTable implements Table {
    public final static String SELECT_TABLES_QUERY = "SELECT  so.name, ss.name AS schema_name, so.create_date " +
            "FROM    sys.objects so " +
            "INNER JOIN sys.schemas ss ON so.schema_id = ss.schema_id " +
            "WHERE so.type = 'U' " +
            "ORDER BY ss.schema_id, so.name";
    private final String url;
    private final String name;
    private final String schema;
    private final Date createDate;
    private final SqlServerDatabase parent;
    private List<Column> columns;

    public SqlServerTable(ResultSet rs, String url, SqlServerDatabase parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("name"));
            this.schema = Validate.notBlank(rs.getString("schema_name"));
            this.createDate = ResultSetUtil.getDate(rs, "create_date");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Column> getColumns() {
        if (columns == null){
            try(Connection con = DriverManager.getConnection(url)) {
                con.setCatalog(parent.getName());
                columns = getColumns(con);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return columns;
    }

    private List<Column> getColumns(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(SqlServerColumn.SELECT_COLUMNS_QUERY, name, schema));

        List<Column> result = new LinkedList<>();
        while (rs.next()) {
            SqlServerColumn column = new SqlServerColumn(rs, url, this);
            result.add(column);
        }

        return result;
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
    public String toString() {
        return "SqlServerTable{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
