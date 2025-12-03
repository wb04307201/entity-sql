
package cn.wubo.sql.forge.records;

/**
 * 列信息记录类
 */
public record ColumnInfo(
        String tableName,
        String columnName,
        String dataType,
        String typeName,
        int columnSize,
        int decimalDigits,
        int nullable,
        String remarks,
        String columnDef,
        int ordinalPosition,
        String isNullable,
        String scopeCatalog,
        String scopeSchema,
        String scopeTable,
        short sourceDataType,
        String isAutoincrement,
        String isGeneratedcolumn
) {}
