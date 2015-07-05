package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Database;
import io.arusland.dbinfo.DbInfoProvider;
import org.apache.commons.lang3.Validate;

import java.sql.*;
import java.util.*;

/**
 * Created by ruslan on 03.07.2015.
 */
public class SqlServerDbInfoProvider implements DbInfoProvider {
    private List<Database> databases;
    private final String url;

    public SqlServerDbInfoProvider(String url) {
        this.url = Validate.notNull(url);
    }

    @Override
    public List<Database> getDatabases() {
        if (databases == null) {
            try (Connection con = DriverManager.getConnection(url)) {
                databases = getDatabases(con);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return databases;
    }

    private List<Database> getDatabases(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(SqlServerDatabase.SELECT_DATABASES_QUERY);

        List<Database> result = new LinkedList<>();
        while (rs.next()) {
            SqlServerDatabase db = new SqlServerDatabase(rs, url);
            result.add(db);
        }
        return result;
    }
}
