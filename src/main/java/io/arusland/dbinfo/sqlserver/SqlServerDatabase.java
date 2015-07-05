package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Database;
import io.arusland.dbinfo.Table;
import io.arusland.dbinfo.util.ResultSetUtil;
import org.apache.commons.lang3.Validate;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ruslan on 04.07.2015.
 */
public class SqlServerDatabase implements Database {
    public static final String SELECT_DATABASES_QUERY = "SELECT name, create_date FROM sys.databases";
    private final Date createDate;
    private final String name;
    private final String url;
    private List<Table> tables;

    public SqlServerDatabase(ResultSet rs, String url) {
        try {
            this.createDate = ResultSetUtil.getDate(rs, "create_date");
            this.name = Validate.notBlank(rs.getString("name"));
            this.url = Validate.notBlank(url);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Table> getTables() {
        if (tables == null) {
            try (Connection con = DriverManager.getConnection(url)) {
                con.setCatalog(name);
                tables = getTables(con);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return tables;
    }

    @Override
    public String getName() {
        return name;
    }

    private List<Table> getTables(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(SqlServerTable.SELECT_TABLES_QUERY);

        List<Table> result = new LinkedList<>();
        while (rs.next()) {
            SqlServerTable table = new SqlServerTable(rs, url, this);
            result.add(table);
        }

        return result;
    }

    @Override
    public String toString() {
        return "SqlServerDatabase{" +
                "name='" + name + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
