package io.arusland.dbinfo;

import java.util.List;

/**
 * Created by ruslan on 03.07.2015.
 */
public interface Table extends BaseObject {
    List<Column> getColumns();

    String getSchema();
}
