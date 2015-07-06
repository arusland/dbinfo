package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Database;
import io.arusland.dbinfo.DbInfoProvider;
import io.arusland.dbinfo.util.DbUtil;
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
                databases = DbUtil.query(con, new DatabaseSupplier(), SqlServerDatabase.SELECT_DATABASES_QUERY);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return databases;
    }

    private class DatabaseSupplier implements DbUtil.Supplier<Database> {
        @Override
        public Database get(ResultSet rs) {
            return new SqlServerDatabase(rs, url);
        }
    }
}
