package cn.wubo.sql.forge.entity.cache;

import lombok.Data;

import java.util.Map;

@Data
public class TableStructureInfo {
    private String tableName;
    private Map<String, ColumnInfo> columnNameColumnInfoMap;
    private Map<String, ColumnInfo> fieldNameColumnInfoMap;
}

