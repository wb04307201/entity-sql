package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.ColumnInfo;
import cn.wubo.sql.forge.records.DatabaseInfo;
import cn.wubo.sql.forge.records.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record CurrentMetaData(
        MetaData metaData
) {

    public CurrentMetaDataTree getCurrentDatabase() throws SQLException {
        CurrentMetaDataTree currentMetaDataTree = new CurrentMetaDataTree();
        DatabaseInfo databaseInfo = metaData.getCurrentDatabase();
        currentMetaDataTree.setDatabaseInfo(databaseInfo);

        List<String> tableTypes = metaData.getTableTypes();
        List<CurrentMetaDataTree.tableTypeTables> tableTypeTables = new ArrayList<>();
        for (String tableType : tableTypes){
            List<TableInfo> tables = metaData.getTables(databaseInfo.catalog(), databaseInfo.schema(), null, new String[]{tableType});

            List<CurrentMetaDataTree.TableColumns> tableColumns = new ArrayList<>();
            for (TableInfo table : tables){
                List<ColumnInfo> columns = metaData.getColumns(databaseInfo.catalog(), databaseInfo.schema(), table.tableName(), null);
                tableColumns.add(new CurrentMetaDataTree.TableColumns(table, columns));
            }
            tableTypeTables.add(new CurrentMetaDataTree.tableTypeTables(tableType, tableColumns));
        }
        currentMetaDataTree.setTableTypes(tableTypeTables);

        return currentMetaDataTree;
    }
}
