package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DataSourceMetaDataTree {

    private DatabaseInfo databaseInfo;

    private List<SchemaTableTypeTables> schemaTableTypeTables;

    @AllArgsConstructor
    @Data
    public static class SchemaTableTypeTables{
        private SchemaInfo schema;
        private List<TableTypeTables> tableTypeTables;
    }

    @AllArgsConstructor
    @Data
    public static class TableTypeTables{
        private String tableType;
        private List<TableColumns> tables;
    }

    @AllArgsConstructor
    @Data
    public static class TableColumns{
        private TableInfo table;
        private List<ColumnInfo> columns;
        private List<PrimaryKeyInfo> primaryKeys;
        private List<ForeignKeyInfo> foreignKeys;
        private List<IndexInfo> indexes;
    }
}
