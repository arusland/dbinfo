package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Column;
import io.arusland.dbinfo.Database;
import io.arusland.dbinfo.Table;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by ruslan on 03.07.2015.
 */
public class SqlServerDbInfoProviderTest extends TestCase {
    public void test(){
        String url = "jdbc:sqlserver://localhost;databaseName=master;user=sa;password=dev1234";
        SqlServerDbInfoProvider provider= new SqlServerDbInfoProvider(url);
        List<Database> dbs = provider.getDatabases();

        for (Database db : dbs){
            System.out.println(db);

            for (Table table : db.getTables()){
                System.out.println("    " + table);
                for (Column column : table.getColumns()){
                    System.out.println("        " + column);
                }
            }
        }
    }

}
