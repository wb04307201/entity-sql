package cn.wubo.sql.forge.records;

public record ForeignKeyInfo(
    String pkTableCatalog,
    String pkTableSchema,
    String pkTableName,
    String pkColumnName,
    String fkTableCatalog,
    String fkTableSchema,
    String fkTableName,
    String fkColumnName,
    short keySequence,
    short updateRule,
    short deleteRule,
    String fkName,
    String pkName,
    short deferrability
) {}
