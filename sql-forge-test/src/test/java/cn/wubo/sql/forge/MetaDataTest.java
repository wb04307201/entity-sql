package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.*;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class MetaDataTest {

    @Autowired
    private MetaData metaData;

    @Test
    void testGetDatabase() throws SQLException {
        DatabaseInfo databaseInfo = metaData.getDatabase();
        log.info("DatabaseInfo: {}", databaseInfo);
        assertNotNull(databaseInfo);
        assertNotNull(databaseInfo.productName());
        assertNotNull(databaseInfo.productVersion());
        assertNotNull(databaseInfo.url());
        assertNotNull(databaseInfo.userName());
        assertNotNull(databaseInfo.driverName());
        assertNotNull(databaseInfo.driverVersion());
    }

    @Test
    void testGetCatalogs() throws SQLException {
        List<CatalogInfo> catalogs = metaData.getCatalogs();
        log.info("Catalogs: {}", catalogs);
        assertNotNull(catalogs);
    }

    @Test
    void testGetSchemas() throws SQLException {
        List<SchemaInfo> schemas = metaData.getSchemas(null, null);
        log.info("Schemas: {}", schemas);
        assertNotNull(schemas);
    }

    @Test
    void testGetTables() throws SQLException {
        List<TableInfo> tables = metaData.getTables(null, null, null, null);
        log.info("Tables: {}", tables);
        assertNotNull(tables);
    }

    @Test
    void testGetColumns() throws SQLException {
        List<CatalogInfo> catalogs = metaData.getCatalogs();
        if (!catalogs.isEmpty()) {
            CatalogInfo catalog = catalogs.get(0);

            List<SchemaInfo> schemas = metaData.getSchemas(catalog.tableCatalog(), "yggztipd_db");
            if (!schemas.isEmpty()) {
                SchemaInfo schema = schemas.get(0);

                List<TableInfo> tables = metaData.getTables(catalog.tableCatalog(), schema.tableSchema(), "project", new String[]{"TABLE"});
                if (!tables.isEmpty()) {
                    TableInfo table = tables.get(0);

                    List<ColumnInfo> columns = metaData.getColumns(table.typeCatalog(), table.typeSchema(), table.tableName(), null);

                    log.info("Columns: {}", columns);

                    assertNotNull(columns);
                } else {
                    log.warn("No tables found in schema: {}", schema.tableSchema());
                }
            } else {
                log.warn("No schemas found in database");
            }
        } else {
            log.warn("No catalogs found");
        }
    }

}