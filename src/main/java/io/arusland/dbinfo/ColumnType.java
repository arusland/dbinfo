package io.arusland.dbinfo;

import org.apache.commons.lang3.Validate;

/**
 * Created by ruslan on 03.07.2015.
 */
public interface ColumnType extends BaseObject {
    Integer getMaxLength();

    boolean isMaxLength();
}
