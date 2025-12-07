package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ExecutorTest {

    @Autowired
    private Executor executor;

    @Test
    void testExecuteQuery() throws SQLException {
        SqlScript sqlScript = new SqlScript("""
                SELECT
                    o.id AS order_id,
                    u.username,
                    p.name AS product_name,
                    p.price,
                    o.quantity,
                    (p.price * o.quantity) AS total
                FROM orders o
                JOIN users u ON o.user_id = u.id
                JOIN products p ON o.product_id = p.id
                """, null);

        List<RowMap> rowMapList = executor.executeQuery(sqlScript);
        log.info("rowMapList: {}", rowMapList);
    }

}
