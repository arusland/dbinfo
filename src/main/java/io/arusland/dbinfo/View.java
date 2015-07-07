package io.arusland.dbinfo;

/**
 * Created by ruslan on 07.07.2015.
 */
public interface View extends BaseObject {
    String getSchema();

    String getContent();
}
