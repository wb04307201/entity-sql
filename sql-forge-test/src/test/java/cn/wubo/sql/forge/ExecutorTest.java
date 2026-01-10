package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.ParamMap;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ExecutorTest {

    @Autowired
    private Executor executor;

    @Autowired
    private TransactionalComponent transactionalComponent;

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

    @Test
    void test() throws SQLException {
        ParamMap params1 = new ParamMap();
        params1.put("550e8400-e29b-41d4-a716-446655440000");
        params1.put("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        params1.put("2025-12-08");
        params1.put("1");

        SqlScript sqlScript1 = new SqlScript("""
                INSERT INTO orders (user_id, product_id, order_date, quantity) VALUES (?,?,?,?)
                """, params1);

        RowMap key = executor.executeInsert(sqlScript1);
        log.info("key: {}", key);
        assertNotNull(key);

        ParamMap params2 = new ParamMap();
        params2.put("2025-12-09");
        params2.put(key.get("ID"));
        SqlScript sqlScript2 = new SqlScript("""
                UPDATE orders SET order_date = ? WHERE id = ?
                """, params2);

        int count = executor.executeUpdate(sqlScript2);
        log.info("count: {}", count);
        assertEquals(1, count);

        ParamMap params3 = new ParamMap();
        params3.put(key.get("ID"));
        SqlScript sqlScript3 = new SqlScript("""
                DELETE FROM orders  WHERE id = ?
                """, params3);
        count = executor.executeUpdate(sqlScript3);
        log.info("count: {}", count);
        assertEquals(1, count);
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public TransactionalComponent transactionalComponent(Executor executor) {
            return new TransactionalComponent(executor);
        }
    }

    @Test
    void testTransactional() throws SQLException {
        try {
            transactionalComponent.insert();
        } catch (SQLException e) {
            log.error("insert error: {}", e.getMessage());
        }

        ParamMap params = new ParamMap();
        params.put("1");
        SqlScript sqlScript = new SqlScript("""
                SELECT * FROM users where id = ?
                """, params);

        List<RowMap> rowMapList = executor.executeQuery(sqlScript);
        assertEquals(0, rowMapList.size());
    }
}
