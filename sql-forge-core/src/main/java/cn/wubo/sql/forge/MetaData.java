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

    /**
     * 获取数据库连接信息
     *
     * @return DatabaseInfo对象，包含数据库的产品名称、版本、连接URL、用户名、驱动名称、
     * 驱动版本、目录和模式等信息
     * @throws SQLException 当获取数据库连接或读取元数据时发生SQL异常
     */
    public DatabaseInfo getCurrentDatabase() throws SQLException {
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


    /**
     * 获取数据库的SQL关键字列表
     *
     * @return 包含数据库SQL关键字的字符串，关键字之间用逗号分隔
     * @throws SQLException 当数据库连接或元数据获取过程中发生错误时抛出
     */
    public String getSQLKeywords() throws SQLException {
        try (Connection connection = getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            return databaseMetaData.getSQLKeywords();
        }
    }

    /**
     * 获取数据库中的所有目录信息
     *
     * @return 包含所有目录信息的列表
     * @throws SQLException 当数据库访问发生错误时抛出
     */
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

    /**
     * 获取指定目录和模式下的数据库模式信息列表
     *
     * @param catalog       数据库目录名称，可为null表示不限制目录
     * @param schemaPattern 模式名称的匹配模式，可为null表示不限制模式
     * @return 包含SchemaInfo对象的列表，每个对象代表一个数据库模式
     * @throws SQLException 当数据库访问发生错误时抛出
     */
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

    /**
     * 获取数据库中所有支持的表类型
     *
     * @return 包含所有表类型的字符串列表
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    public List<String> getTableTypes() throws SQLException {
        try (Connection connection = getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getTableTypes()) {
                List<String> tableTypes = new ArrayList<>();
                while (rs.next()) {
                    tableTypes.add(rs.getString("TABLE_TYPE"));
                }
                return tableTypes;
            }
        }
    }

    /**
     * 获取数据库中的表信息列表
     *
     * @param catalog          表所在的目录名称，如果为null则表示所有目录
     * @param schemaPattern    表模式名称的匹配模式，如果为null则表示所有模式
     * @param tableNamePattern 表名称的匹配模式，如果为null则表示所有表
     * @param types            要包含的表类型数组，如"TABLE"、"VIEW"等，如果为null则表示所有类型
     * @return 包含表信息的TableInfo对象列表
     * @throws SQLException 当数据库访问发生错误时抛出
     */
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

    /**
     * 获取数据库表的列信息
     *
     * @param catalog           数据库目录名称，如果为null则表示所有目录
     * @param schemaPattern     模式名称模式，如果为null则表示所有模式
     * @param tableNamePattern  表名模式，如果为null则表示所有表
     * @param columnNamePattern 列名模式，如果为null则表示所有列
     * @return 包含列信息的列表
     * @throws SQLException 当数据库访问发生错误时抛出
     */
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

    /**
     * 获取指定表的主键信息列表
     *
     * @param catalog 数据库目录名称，可为null
     * @param schema  数据库模式名称，可为null
     * @param table   表名称，不能为空
     * @return 主键信息列表，包含表的所有主键列信息
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    public List<PrimaryKeyInfo> getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        try (Connection connection = getConnection()) {
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
    }

    /**
     * 获取指定表的外键信息列表
     *
     * @param catalog 数据库目录名称，可为null
     * @param schema  数据库模式名称，可为null
     * @param table   表名称，不能为null
     * @return 外键信息列表
     * @throws SQLException 数据库访问异常
     */
    public List<ForeignKeyInfo> getImportedKeys(String catalog, String schema, String table) throws SQLException {
        try (Connection connection = getConnection()) {
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
    }

    /**
     * 获取指定表的索引信息
     *
     * @param catalog     数据库目录名称，如果为null则表示获取所有目录
     * @param schema      数据库模式名称，如果为null则表示获取所有模式
     * @param table       表名称，不能为空
     * @param unique      是否只返回唯一索引，true表示只返回唯一索引，false表示返回所有索引
     * @param approximate 是否允许返回近似值，true表示允许返回近似值，false表示要求精确值
     * @return 包含索引信息的列表
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    public List<IndexInfo> getIndexInfo(String catalog, String schema, String table,
                                        boolean unique, boolean approximate) throws SQLException {
        try (Connection connection = getConnection()) {
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
    }


}