package io.arusland.dbinfo.util;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ruslan on 05.07.2015.
 */
public class DbUtil {
    public static Date getDate(ResultSet rs, String name) throws SQLException {
        Timestamp ts = rs.getTimestamp(name);

        return new Date(ts.getTime());
    }

    public static <T>  List<T> query(Connection con, Supplier<T> supplier, String query){
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(query);

            final List<T> result = new LinkedList<>();
            while (rs.next()) {
                result.add(supplier.get(rs));
            }

            return result;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static interface Supplier<T> {
        T get(ResultSet rs);
    }
}
