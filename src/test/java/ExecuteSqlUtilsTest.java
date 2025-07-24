import cn.wubo.sql.util.ExecuteSqlUtils;
import cn.wubo.sql.util.MutilConnectionPool;
import cn.wubo.sql.util.TypeReference;
import cn.wubo.sql.util.web.EntityWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = EntityWebConfig.class)
class ExecuteSqlUtilsTest {

    @Test
    void test() throws SQLException {
        // 判断数据源是否加载
        if (Boolean.FALSE.equals(MutilConnectionPool.check("test"))) {
            // 加载数据源
            MutilConnectionPool.init("test", "jdbc:h2:file:./data/demo;AUTO_SERVER=TRUE", "sa", "");
        }

        Connection connection = MutilConnectionPool.getConnection("test");

        // 判断表是否存在
        Boolean check = ExecuteSqlUtils.isTableExists(connection, "test_user".toUpperCase());
        if(check){
            ExecuteSqlUtils.executeUpdate(connection, "drop table test_user");
        }
        ExecuteSqlUtils.executeUpdate(connection, "create table test_user (id VARCHAR(200),user_name VARCHAR(20),department VARCHAR(200),birth DATE,age NUMBER(10),amount NUMBER(10,2),status VARCHAR(1))");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, "123123");
        ExecuteSqlUtils.executeUpdate(connection, "INSERT INTO test_user (user_name) VALUES (?)", params);

        params = new HashMap<>();
        params.put(1, "321123");
        // 执行插入、更新的sql语句
        int count = ExecuteSqlUtils.executeUpdate(connection, "update test_user set user_name = ?", params);

        // 执行查询的sql语句
        List<Map<String, Object>> list = ExecuteSqlUtils.executeQuery(connection, "select * from test_user where user_name = ?", params, new TypeReference<>() {
        });

        // 执行删除的sql语句
        ExecuteSqlUtils.executeUpdate(connection, "delete from test_user where user_name = ?", params);

        connection.close();
    }

}