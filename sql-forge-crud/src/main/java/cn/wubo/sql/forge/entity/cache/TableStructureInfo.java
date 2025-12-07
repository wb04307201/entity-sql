package cn.wubo.sql.forge.entity.cache;

import cn.wubo.sql.forge.entity.inter.SFunction;
import cn.wubo.sql.forge.entity.utils.LambdaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Slf4j
@Data
public class TableStructureInfo {
    private String tableName;
    private Map<String, ColumnInfo> columnNameColumnInfoMap;
    private Map<String, ColumnInfo> fieldNameColumnInfoMap;

    public <T> ColumnInfo getColumnInfo(SFunction<T, ?> fn) {
        try {
            String fieldName = LambdaUtils.getFieldName(fn);
            return fieldNameColumnInfoMap.getOrDefault(fieldName,null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }
}

