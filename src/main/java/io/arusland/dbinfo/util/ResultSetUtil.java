package io.arusland.dbinfo.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by ruslan on 05.07.2015.
 */
public class ResultSetUtil {
    public static Date getDate(ResultSet rs, String name) throws SQLException {
        Timestamp ts = rs.getTimestamp(name);

        return new Date(ts.getTime());
    }
}
