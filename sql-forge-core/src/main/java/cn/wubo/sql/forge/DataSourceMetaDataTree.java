package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.ColumnInfo;
import cn.wubo.sql.forge.records.DatabaseInfo;
import cn.wubo.sql.forge.records.SchemaInfo;
import cn.wubo.sql.forge.records.TableInfo;
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
    }
}
