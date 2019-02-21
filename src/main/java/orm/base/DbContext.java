package orm.base;

import java.sql.SQLException;
import java.util.List;

public interface DbContext<T> {

    boolean persist(T entity) throws IllegalAccessException, SQLException;

    List<T> find() throws SQLException;

    List<T> find(String where) throws SQLException;

    T findFirst() throws SQLException;

    T findFirst(String where) throws SQLException;
}
