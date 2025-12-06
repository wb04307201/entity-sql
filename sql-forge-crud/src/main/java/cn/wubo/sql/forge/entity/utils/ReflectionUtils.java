package cn.wubo.sql.forge.entity.utils;

import cn.wubo.sql.forge.entity.inter.SFunction;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@UtilityClass
public class ReflectionUtils {

    public static String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            String tableName = tableAnnotation.name();
            if (!tableName.isEmpty()) {
                return tableName;
            }
        }
        return StringUtils.camelToUnderscore(entityClass.getSimpleName());
    }

    public <T> String getColumnName(Class<?> entityClass, SFunction<T, ?> fn) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String fieldName = LambdaUtils.getFieldName(fn);
        Field field = entityClass.getDeclaredField(fieldName);
        if (field.isAnnotationPresent(Transient.class)) {
            return null;
        }
        if (field.isAnnotationPresent(Column.class)) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            String columnName = columnAnnotation.name();
            if (!columnName.isEmpty()) {
                return columnName;
            }
        }
        return StringUtils.camelToUnderscore(fieldName);
    }
}
