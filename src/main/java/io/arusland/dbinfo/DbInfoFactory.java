package io.arusland.dbinfo;

import io.arusland.dbinfo.sqlserver.SqlServerDbInfoProvider;

/**
 * Created by ruslan on 03.07.2015.
 */
public class DbInfoFactory {
    public static DbInfoProvider create(DbType dbType, String url) {
        switch (dbType) {
            case SQL_SERVER:
                return new SqlServerDbInfoProvider(url);
            default:
                throw new UnsupportedOperationException("Unsupported dbType: " + dbType);
        }
    }
}
