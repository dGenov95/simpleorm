package orm;

import constants.QueryTemplates;
import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.PrimaryKey;
import orm.base.DbContext;
import orm.util.EntityCreator;
import orm.util.IEntityCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EntityManager<T> implements DbContext<T> {


    private Class<T> klass;
    private Connection connection;

    public EntityManager(Class<T> klass, Connection connection) {
        this.klass = klass;
        this.connection = connection;
    }

    @Override
    public boolean persist(T entity) throws IllegalAccessException, SQLException {
        Field primaryKeyColumnField = getPrimaryKeyColumnField();
        primaryKeyColumnField.setAccessible(true);
        long primaryKeyValue = (long) primaryKeyColumnField.get(entity);
        if (primaryKeyValue == 0) {
            return insert(entity);
        }
        return update(entity);
    }

    @Override
    public List<T> find() throws SQLException {
        return find(null);
    }

    @Override
    public List<T> find(String where) throws SQLException {
        String template = where == null ? QueryTemplates.BASE_SELECT_FROM_TEMPLATE : QueryTemplates.BASE_SELECT_FROM_WHERE_TEMPLATE;
        return find(template, where);
    }

    @Override
    public T findFirst() throws SQLException {
        return findFirst(null);
    }

    @Override
    public T findFirst(String where) throws SQLException {
        String template = where == null ? QueryTemplates.BASE_SINGLE_SELECT_QUERY : QueryTemplates.BASE_SINGLE_SELECT_WHERE_QUERY;
        return find(template, where).get(0);
    }

    private List<T> find(String template, String where) throws SQLException {
        String query = MessageFormat.format(template, getTableName(), where);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        return toList(resultSet);
    }

    private boolean insert(T entity) throws SQLException {
        String columnNames = getColumnFields().stream()
                .map(field -> field.getAnnotation(Column.class).name())
                .collect(Collectors.joining(","));
        String columnValues = getColumnFields().stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        if (value instanceof String) {
                            return "\'" + value + "\'";
                        }
                        return value;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    field.setAccessible(false);
                    return null;
                })
                .map(Objects::toString)
                .collect(Collectors.joining(","));

        String query = MessageFormat.format(QueryTemplates.BASE_INSERT_TEMPLATE, getTableName(), columnNames, columnValues);
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.execute();
    }

    private boolean update(T entity) {

        return false;
    }

    private List<T> toList(ResultSet resultSet) throws SQLException {
        IEntityCreator<T> entityCreator = new EntityCreator<>(resultSet, klass);
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T entity = entityCreator.createEntity();
            resultList.add(entity);
        }
        return resultList;
    }

    private String getTableName() {
        Annotation entity = Arrays.stream(klass.getAnnotations())
                .filter(annotation -> annotation.annotationType() == Entity.class)
                .findAny()
                .orElse(null);
        if (entity == null) {
            return klass.getSimpleName() + "s";
        }

        return klass.getAnnotation(Entity.class).tableName();
    }

    private Field getPrimaryKeyColumnField() {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Primary key not found"));
    }

    private List<Field> getColumnFields() {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());
    }
}
