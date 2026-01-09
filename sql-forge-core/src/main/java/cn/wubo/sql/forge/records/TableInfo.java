package cn.wubo.sql.forge.records;

/**
 * 表信息记录类
 */
public record TableInfo(
        String tableName,
        String tableType,
        String remarks,
        String typeCatalog,
        String typeSchema,
        String typeName,
        String selfReferencingColName,
        String refGeneration
) {
}