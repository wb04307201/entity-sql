package cn.wubo.sql.forge.utils;

import cn.wubo.sql.forge.DataSourceMetaDataTree;
import cn.wubo.sql.forge.records.*;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MetaDataUtils {

    public DatabaseInfo getDatabase(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        return new DatabaseInfo(
                databaseMetaData.getDatabaseProductName(),
                databaseMetaData.getDatabaseProductVersion(),
                databaseMetaData.getURL(),
                databaseMetaData.getUserName(),
                databaseMetaData.getDriverName(),
                databaseMetaData.getDriverVersion()
        );
    }

    public String getSQLKeywords(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        return databaseMetaData.getSQLKeywords();
    }

    public List<CatalogInfo> getCatalogs(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getCatalogs()) {
            List<CatalogInfo> catalogs = new ArrayList<>();
            while (rs.next()) {
                catalogs.add(new CatalogInfo(
                        rs.getString("TABLE_CAT")
                ));
            }
            return catalogs;
        }
    }

    public List<SchemaInfo> getSchemas(Connection connection, String catalog, String schemaPattern) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getSchemas(catalog, schemaPattern)) {
            List<SchemaInfo> schemas = new ArrayList<>();
            while (rs.next()) {
                schemas.add(new SchemaInfo(
                        rs.getString("TABLE_SCHEM"),
                        rs.getString("TABLE_CATALOG")
                ));
            }
            return schemas;
        }
    }

    public List<String> getTableTypes(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getTableTypes()) {
            List<String> tableTypes = new ArrayList<>();
            while (rs.next()) {
                tableTypes.add(rs.getString("TABLE_TYPE"));
            }
            return tableTypes;
        }
    }

    public List<TableInfo> getTables(Connection connection, String catalog, String schemaPattern,
                                     String tableNamePattern, String[] types) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types)) {
            List<TableInfo> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(new TableInfo(
                        rs.getString("TABLE_NAME"),
                        rs.getString("TABLE_TYPE"),
                        rs.getString("REMARKS"),
                        rs.getString("TYPE_CAT"),
                        rs.getString("TYPE_SCHEM"),
                        rs.getString("TYPE_NAME"),
                        rs.getString("SELF_REFERENCING_COL_NAME"),
                        rs.getString("REF_GENERATION")
                ));
            }
            return tables;
        }
    }

    public List<ColumnInfo> getColumns(Connection connection, String catalog, String schemaPattern,

                                       String tableNamePattern, String columnNamePattern) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)) {
            List<ColumnInfo> columns = new ArrayList<>();
            while (rs.next()) {
                columns.add(new ColumnInfo(
                        rs.getString("TABLE_NAME"),
                        rs.getString("COLUMN_NAME"),
                        rs.getInt("DATA_TYPE"),
                        ReflectUtils.getJavaSqlTypeName(rs.getInt("DATA_TYPE")),
                        rs.getString("TYPE_NAME"),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getInt("DECIMAL_DIGITS"),
                        rs.getInt("NULLABLE"),
                        rs.getString("REMARKS"),
                        rs.getString("COLUMN_DEF"),
                        rs.getInt("ORDINAL_POSITION"),
                        rs.getString("IS_NULLABLE"),
                        rs.getString("SCOPE_CATALOG"),
                        rs.getString("SCOPE_SCHEMA"),
                        rs.getString("SCOPE_TABLE"),
                        rs.getShort("SOURCE_DATA_TYPE"),
                        rs.getString("IS_AUTOINCREMENT"),
                        rs.getString("IS_GENERATEDCOLUMN")
                ));
            }
            return columns;
        }
    }

    public List<PrimaryKeyInfo> getPrimaryKeys(Connection connection, String catalog, String schema, String table) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, schema, table)) {
            List<PrimaryKeyInfo> primaryKeys = new ArrayList<>();
            while (rs.next()) {
                primaryKeys.add(new PrimaryKeyInfo(
                        rs.getString("TABLE_CAT"),
                        rs.getString("TABLE_SCHEM"),
                        rs.getString("TABLE_NAME"),
                        rs.getString("COLUMN_NAME"),
                        rs.getShort("KEY_SEQ"),
                        rs.getString("PK_NAME")
                ));
            }
            return primaryKeys;
        }
    }

    public List<ForeignKeyInfo> getImportedKeys(Connection connection, String catalog, String schema, String table) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getImportedKeys(catalog, schema, table)) {
            List<ForeignKeyInfo> foreignKeys = new ArrayList<>();
            while (rs.next()) {
                foreignKeys.add(new ForeignKeyInfo(
                        rs.getString("PKTABLE_CAT"),
                        rs.getString("PKTABLE_SCHEM"),
                        rs.getString("PKTABLE_NAME"),
                        rs.getString("PKCOLUMN_NAME"),
                        rs.getString("FKTABLE_CAT"),
                        rs.getString("FKTABLE_SCHEM"),
                        rs.getString("FKTABLE_NAME"),
                        rs.getString("FKCOLUMN_NAME"),
                        rs.getShort("KEY_SEQ"),
                        rs.getShort("UPDATE_RULE"),
                        rs.getShort("DELETE_RULE"),
                        rs.getString("FK_NAME"),
                        rs.getString("PK_NAME"),
                        rs.getShort("DEFERRABILITY")
                ));
            }
            return foreignKeys;
        }
    }

    public List<IndexInfo> getIndexInfo(Connection connection, String catalog, String schema, String table,
                                        boolean unique, boolean approximate) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        try (ResultSet rs = databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate)) {
            List<IndexInfo> indexes = new ArrayList<>();
            while (rs.next()) {
                indexes.add(new IndexInfo(
                        rs.getString("TABLE_CAT"),
                        rs.getString("TABLE_SCHEM"),
                        rs.getString("TABLE_NAME"),
                        rs.getBoolean("NON_UNIQUE"),
                        rs.getString("INDEX_QUALIFIER"),
                        rs.getString("INDEX_NAME"),
                        rs.getShort("TYPE"),
                        rs.getShort("ORDINAL_POSITION"),
                        rs.getString("COLUMN_NAME"),
                        rs.getString("ASC_OR_DESC"),
                        rs.getLong("CARDINALITY"),
                        rs.getLong("PAGES"),
                        rs.getString("FILTER_CONDITION")
                ));
            }
            return indexes;
        }
    }

    public DataSourceMetaDataTree getDataSourceMetaDataTree(Connection connection) throws SQLException {
        DataSourceMetaDataTree dataSourceMetaDataTree = new DataSourceMetaDataTree();
        DatabaseInfo databaseInfo = getDatabase(connection);
        dataSourceMetaDataTree.setDatabaseInfo(databaseInfo);

        List<String> tableTypes = getTableTypes(connection);
        List<SchemaInfo> schemas = getSchemas(connection, null,null);

        List<DataSourceMetaDataTree.SchemaTableTypeTables> schemaTableTypeTables = new ArrayList<>();
        for (SchemaInfo schema : schemas){
            List<DataSourceMetaDataTree.TableTypeTables> tableTypeTables = new ArrayList<>();
            for (String tableType : tableTypes) {
                List<TableInfo> tables = getTables(connection, schema.tableCatalog(), schema.tableSchema(), null, new String[]{tableType});
                List<DataSourceMetaDataTree.TableColumns> tableColumns = new ArrayList<>();
                for (TableInfo table : tables) {
                    List<ColumnInfo> columns = getColumns(connection, schema.tableCatalog(), schema.tableSchema(), table.tableName(), null);
                    List<PrimaryKeyInfo> primaryKeys = getPrimaryKeys(connection, schema.tableCatalog(), schema.tableSchema(), table.tableName());
                    List<ForeignKeyInfo> foreignKeys = getImportedKeys(connection, schema.tableCatalog(), schema.tableSchema(), table.tableName());
                    List<IndexInfo> indexes = getIndexInfo(connection, schema.tableCatalog(), schema.tableSchema(), table.tableName(), false, false);
                    tableColumns.add(new DataSourceMetaDataTree.TableColumns(table, columns, primaryKeys, foreignKeys, indexes));
                }
                tableTypeTables.add(new DataSourceMetaDataTree.TableTypeTables(tableType, tableColumns));
            }
            schemaTableTypeTables.add(new DataSourceMetaDataTree.SchemaTableTypeTables(schema, tableTypeTables));
        }
        dataSourceMetaDataTree.setSchemaTableTypeTables(schemaTableTypeTables);

        return dataSourceMetaDataTree;
    }
}
