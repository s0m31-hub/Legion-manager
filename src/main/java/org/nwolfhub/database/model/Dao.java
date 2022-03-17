package org.nwolfhub.database.model;

import java.util.List;

public interface Dao {
    Object get(Class class2pick, Integer id);
    void save(Object obj);
    void update(Object obj);
    void delete(Object obj);
    List getAll(String table);
}
