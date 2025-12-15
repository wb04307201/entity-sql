package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.ColumnInfo;
import cn.wubo.sql.forge.records.DatabaseInfo;
import cn.wubo.sql.forge.records.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class CurrentMetaDataTree {

    private DatabaseInfo databaseInfo;

    private List<tableTypeTables> tableTypes;

    @AllArgsConstructor
    @Data
    public static class tableTypeTables{
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
