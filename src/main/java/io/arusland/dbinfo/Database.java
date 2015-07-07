package io.arusland.dbinfo;

import java.util.List;
import java.util.function.*;

/**
 * Created by ruslan on 03.07.2015.
 */
public interface Database extends BaseObject {
    List<Table> getTables();

    List<Procedure> getProcedures();

    List<Function> getFunctions();

    List<View> getViews();
}
