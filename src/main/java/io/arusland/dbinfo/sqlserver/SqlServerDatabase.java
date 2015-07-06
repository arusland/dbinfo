package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Database;
import io.arusland.dbinfo.Procedure;
import io.arusland.dbinfo.Table;
import io.arusland.dbinfo.util.DbUtil;
import org.apache.commons.lang3.Validate;

import java.sql.*;
import java.util.Date;
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
    private List<Procedure> procedures;

    public SqlServerDatabase(ResultSet rs, String url) {
        try {
            this.createDate = DbUtil.getDate(rs, "create_date");
            this.name = Validate.notBlank(rs.getString("name"));
            this.url = Validate.notBlank(url);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Table> getTables() {
        if (tables == null) {
            loadObjects();
        }

        return tables;
    }

    @Override
    public List<Procedure> getProcedures() {
        if (procedures == null){
            loadObjects();
        }

        return procedures;
    }

    @Override
    public String getName() {
        return name;
    }

    private void loadObjects() {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setCatalog(name);
            tables = DbUtil.query(con, new TableSupplier(), SqlServerTable.SELECT_TABLES_QUERY);
            procedures = DbUtil.query(con, new ProcedureSupplier(), SqlServerProcedure.SELECT_PROCEDURES_QUERY);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private class ProcedureSupplier implements DbUtil.Supplier<Procedure>{
        @Override
        public Procedure get(ResultSet rs) {
            return new SqlServerProcedure(rs, url, SqlServerDatabase.this);
        }
    }

    private class TableSupplier implements DbUtil.Supplier<Table>{
        @Override
        public Table get(ResultSet rs) {
            return new SqlServerTable(rs, url, SqlServerDatabase.this);
        }
    }

    public String toString() {
        return "SqlServerDatabase{" +
                "name='" + name + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
