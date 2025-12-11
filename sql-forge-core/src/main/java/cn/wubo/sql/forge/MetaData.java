package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.*;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record MetaData(DataSource dataSource) {

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    public DatabaseInfo getDatabase() throws SQLException {
        try (Connection connection = getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            return new DatabaseInfo(
                    databaseMetaData.getDatabaseProductName(),
                    databaseMetaData.getDatabaseProductVersion(),
                    databaseMetaData.getURL(),
                    databaseMetaData.getUserName(),
                    databaseMetaData.getDriverName(),
                    databaseMetaData.getDriverVersion(),
                    connection.getCatalog(),
                    connection.getSchema()
            );
        }
    }

    public List<CatalogInfo> getCatalogs() throws SQLException {
        try (Connection connection = getConnection()) {
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
    }

    public List<SchemaInfo> getSchemas(String catalog, String schemaPattern) throws SQLException {
        try (Connection connection = getConnection()) {
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
    }

    public List<TableInfo> getTables(String catalog, String schemaPattern,
                                     String tableNamePattern, String[] types) throws SQLException {
        try (Connection connection = getConnection()) {
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
    }

    public List<ColumnInfo> getColumns(String catalog, String schemaPattern,
                                       String tableNamePattern, String columnNamePattern) throws SQLException {
        try (Connection connection = getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)) {
                List<ColumnInfo> columns = new ArrayList<>();
                while (rs.next()) {
                    columns.add(new ColumnInfo(
                            rs.getString("TABLE_NAME"),
                            rs.getString("COLUMN_NAME"),
                            rs.getString("DATA_TYPE"),
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
    }
}