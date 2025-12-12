package cn.wubo.sql.forge.records;

public record PrimaryKeyInfo(
    String tableCatalog,
    String tableSchema,
    String tableName,
    String columnName,
    short keySequence,
    String pkName
) {}
