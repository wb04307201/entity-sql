package cn.wubo.sql.forge;

import cn.wubo.sql.forge.utils.MetaDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class DataSourceMetaDataTreeTest {

    @Autowired
    DataSource dataSource;

    @Test
    void test() throws SQLException {
        DataSourceMetaDataTree dataSourceMetaDataTree = MetaDataUtils.getDataSourceMetaDataTree(dataSource.getConnection());
        log.info("{}", dataSourceMetaDataTree);
    }
}
