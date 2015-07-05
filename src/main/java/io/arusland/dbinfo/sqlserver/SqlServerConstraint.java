package io.arusland.dbinfo.sqlserver;

import io.arusland.dbinfo.Constraint;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Created by ruslan on 05.07.2015.
 */
public class SqlServerConstraint implements Constraint {
    private final String name;
    private final String type;
    private final String definition;

    public SqlServerConstraint(String name, String type, String definition) {
        this.name = Validate.notBlank(name);
        this.type = Validate.notBlank(type);
        this.definition = definition != null ? definition : StringUtils.EMPTY;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "SqlServerConstraint{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                (StringUtils.isNotBlank(definition) ? ", definition='" + definition + '\'' : "") +
                '}';
    }
}
