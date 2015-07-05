package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.BaseObject;
import io.arusland.dbinfo.ColumnType;
import org.apache.commons.lang3.Validate;

/**
 * Created by ruslan on 03.07.2015.
 */
public class SqlServerColumnType implements ColumnType {
    private final String name;
    private final Integer maxLength;
    private final Integer numericPrecision;
    private final Integer numericScale;

    public SqlServerColumnType(String name, Integer maxLength, Integer numericPrecision, Integer numericScale) {
        this.maxLength = maxLength;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.name = Validate.notBlank(name);
    }

    @Override
    public String getName() {
        return name;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public boolean isMaxLength() {
        return maxLength != null && maxLength == -1;
    }

    @Override
    public String toString() {
        if (getMaxLength() != null) {
            return name + "(" + (isMaxLength() ? "max" : getMaxLength()) + ")";
        } else if (numericPrecision != null && numericScale != null && "decimal".equals(name)) {
            return name + "(" + numericPrecision + "," + numericScale + ")";
        }

        return name;
    }
}
