package io.arusland.dbinfo;

import java.util.List;

/**
 * Created by ruslan on 03.07.2015.
 */
public interface DbInfoProvider {
    List<Database> getDatabases();
}
