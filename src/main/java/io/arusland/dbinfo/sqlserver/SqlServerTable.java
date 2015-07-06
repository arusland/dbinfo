package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Column;
import io.arusland.dbinfo.Constraint;
import io.arusland.dbinfo.Table;
import io.arusland.dbinfo.util.DbUtil;
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
    private final static String SELECT_TABLE_ID = "SELECT object_id " +
            "FROM sys.tables t " +
            "INNER JOIN sys.schemas ss on t.schema_id = ss.schema_id " +
            "WHERE t.name = N'%s' and ss.name = N'%s'";
    private final static String SELECT_DEFAULT_CONSTRAINTS_QUERY = "SELECT name, type_desc, definition FROM sys.default_constraints " +
            "WHERE parent_object_id = %d";
    private final static String SELECT_KEY_CONSTRAINTS_QUERY = "SELECT name, type_desc FROM sys.key_constraints " +
            "WHERE parent_object_id = %d";
    private final static String SELECT_CHECK_CONSTRAINTS_QUERY = "SELECT name, type_desc, definition FROM sys.check_constraints " +
            "WHERE parent_object_id = %d";
    private static final String SELECT_FK_CONSTRAINTS_QUERY = "SELECT name, type_desc FROM sys.foreign_keys " +
            "WHERE parent_object_id = %d";
    ;
    private final String url;
    private final String name;
    private final String schema;
    private final Date createDate;
    private final SqlServerDatabase parent;
    private List<Column> columns;
    private List<Constraint> constraints;

    public SqlServerTable(ResultSet rs, String url, SqlServerDatabase parent) {
        try {
            this.url = Validate.notBlank(url);
            this.parent = Validate.notNull(parent);
            this.name = Validate.notBlank(rs.getString("name"));
            this.schema = Validate.notBlank(rs.getString("schema_name"));
            this.createDate = DbUtil.getDate(rs, "create_date");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Column> getColumns() {
        if (columns == null) {
            getTableObjects();
        }

        return columns;
    }

    @Override
    public List<Constraint> getConstraints() {
        if (constraints == null) {
            getTableObjects();
        }

        return constraints;
    }

    private void getTableObjects() {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setCatalog(parent.getName());
            columns = DbUtil.query(con, new ColumnSupplier(),
                    String.format(SqlServerColumn.SELECT_COLUMNS_QUERY, name, schema));
            constraints = getConstraints(con);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Constraint> getConstraints(Connection con) throws SQLException {
        final List<Constraint> result = new LinkedList<>();
        Long tableId = getScalarLong(con, "object_id", String.format(SELECT_TABLE_ID, name, schema));

        if (tableId != null) {
            result.addAll(DbUtil.query(con, new ConstraintSupplier(true),
                    String.format(SELECT_DEFAULT_CONSTRAINTS_QUERY, tableId)));
            result.addAll(DbUtil.query(con, new ConstraintSupplier(true),
                    String.format(SELECT_CHECK_CONSTRAINTS_QUERY, tableId)));
            result.addAll(DbUtil.query(con, new ConstraintSupplier(false),
                    String.format(SELECT_KEY_CONSTRAINTS_QUERY, tableId)));
            result.addAll(DbUtil.query(con, new ConstraintSupplier(false),
                    String.format(SELECT_FK_CONSTRAINTS_QUERY, tableId)));
        }

        return result;
    }

    private Long getScalarLong(Connection con, String name, String sql) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            return rs.getLong(name);
        }

        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    private class ColumnSupplier implements DbUtil.Supplier<Column> {
        @Override
        public Column get(ResultSet rs) {
            return new SqlServerColumn(rs, url, SqlServerTable.this);
        }
    }

    private class ConstraintSupplier implements DbUtil.Supplier<Constraint> {
        private final boolean withDefinition;

        private ConstraintSupplier(boolean withDefinition) {
            this.withDefinition = withDefinition;
        }

        @Override
        public Constraint get(ResultSet rs) {
            try {
                final String definition = withDefinition ? rs.getString("definition") : null;

                return new SqlServerConstraint(rs.getString("name"), rs.getString("type_desc"), definition);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
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
