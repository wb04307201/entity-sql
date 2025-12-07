package cn.wubo.sql.forge.entity.utils;

import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;
import cn.wubo.sql.forge.entity.inter.SFunction;
import jakarta.persistence.*;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@UtilityClass
public class ReflectionUtils {

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

    public TableStructureInfo extractTableInfo(Class<?> entityClass) {
        TableStructureInfo info = new TableStructureInfo();

        // 获取@Table注解
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            info.setTableName(tableAnnotation.name());
        } else {
            info.setTableName(StringUtils.camelToUnderscore(entityClass.getSimpleName()));
        }

        // 遍历所有字段
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setField(field);
            columnInfo.setFieldName(field.getName());
            columnInfo.setJavaType(field.getType());

            // 获取@Column注解
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                columnInfo.setColumnName(columnAnnotation.name().isEmpty() ?
                        StringUtils.camelToUnderscore(field.getName()) : columnAnnotation.name());
                columnInfo.setNullable(columnAnnotation.nullable());
                columnInfo.setColumnDefinition(columnAnnotation.columnDefinition());
                columnInfo.setLength(columnAnnotation.length());
                columnInfo.setPrecision(columnAnnotation.precision());
                columnInfo.setScale(columnAnnotation.scale());
                columnInfo.setComment(columnAnnotation.comment());
            } else {
                columnInfo.setColumnName(StringUtils.camelToUnderscore(field.getName()));
            }

            // 检查是否为主键
            if (field.isAnnotationPresent(Id.class)) {
                columnInfo.setPrimaryKey(true);
            }

            // 检查是否为大对象
            if (field.isAnnotationPresent(Lob.class)) {
                columnInfo.setLob(true);
            }

            info.getColumnNameColumnInfoMap().put(columnInfo.getColumnName(), columnInfo);
            info.getFieldNameColumnInfoMap().put(columnInfo.getFieldName(), columnInfo);
        }

        return info;
    }

}
