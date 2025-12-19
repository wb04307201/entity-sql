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
        DataSourceMetaDataTree dataSourceMetaDataTree = metaData.getCurrentDatabase();
        log.info("DatabaseInfo: {}", dataSourceMetaDataTree);
    }

}