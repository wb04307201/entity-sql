package cn.wubo.sql.forge.records;

public record IndexInfo(
    String tableCatalog,
    String tableSchema,
    String tableName,
    boolean nonUnique,
    String indexQualifier,
    String indexName,
    short type,
    short ordinalPosition,
    String columnName,
    String ascOrDesc,
    long cardinality,
    long pages,
    String filterCondition
) {}
