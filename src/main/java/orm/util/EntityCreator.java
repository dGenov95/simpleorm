package orm.util;

import orm.annotations.Column;
import orm.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class EntityCreator<T> implements IEntityCreator<T> {

    private ResultSet resultSet;
    private Class<T> klass;

    public EntityCreator(ResultSet resultSet, Class<T> klass){
        this.resultSet = resultSet;
        this.klass = klass;
    }

    @Override
    public T createEntity() {
        T instance = null;
        try {
            instance = klass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        List<Field> columnFields = getColumnFields();
        Field primaryKey = getPrimaryKeyColumnField();
        setPrimaryKeyValue(resultSet, instance, primaryKey);
        primaryKey.setAccessible(false);
        setFieldValues(resultSet, instance, columnFields);
        return instance;
    }

    private void setPrimaryKeyValue(ResultSet resultSet, T instance, Field primaryKey) {
        primaryKey.setAccessible(true);
        String primaryKeyColumnName = primaryKey.getAnnotation(PrimaryKey.class).columnName();
        long primaryKeyValue = 0;
        try {
            primaryKeyValue = resultSet.getLong(primaryKeyColumnName);
            primaryKey.set(instance, primaryKeyValue);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void setFieldValues(ResultSet resultSet, T instance, List<Field> columnFields) {
        columnFields.forEach(field -> {
            field.setAccessible(true);
            String fieldColumnName = field.getAnnotation(Column.class).name();
            Class<?> fieldType = field.getType();
            try {
                if (fieldType == Long.class || fieldType == long.class) {
                    long fieldValue = resultSet.getLong(fieldColumnName);
                    field.set(instance, fieldValue);
                }else if(fieldType == Integer.class || fieldType == int.class){
                    int fieldValue = resultSet.getInt(fieldColumnName);
                    field.set(instance,fieldValue);
                }
                else if (fieldType == String.class) {
                    String fieldValue = resultSet.getString(fieldColumnName);
                    field.set(instance, fieldValue);
                } else if (fieldType == Date.class) {
                    Date fieldValue = resultSet.getDate(fieldColumnName);
                    field.set(instance, fieldValue);
                }
            } catch (SQLException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private List<Field> getColumnFields() {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());
    }

    private Field getPrimaryKeyColumnField() {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Primary key not found"));
    }
}
