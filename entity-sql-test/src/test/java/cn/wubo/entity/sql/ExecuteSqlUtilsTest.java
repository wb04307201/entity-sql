package cn.wubo.entity.sql;

import cn.wubo.entity.sql.utils.ExecuteSqlUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteSqlUtilsTest {

    @Test
    void test() throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        Connection connection = dataSource.getConnection();

        Boolean check = ExecuteSqlUtils.isTableExists(connection, null, null, "TEST_USER", new String[]{"TABLE"});
        if (check) {
            ExecuteSqlUtils.executeUpdate(connection, "drop table test_user", null);
        }
        ExecuteSqlUtils.executeUpdate(connection, "create table test_user (id VARCHAR(200),user_name VARCHAR(20),department VARCHAR(200),birth DATE,age NUMBER(10),amount NUMBER(10,2),status VARCHAR(1))", null);

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, "123123");
        ExecuteSqlUtils.executeUpdate(connection, "INSERT INTO test_user (user_name) VALUES (?)", params);

        params = new HashMap<>();
        params.put(1, "321123");
        // 执行插入、更新的sql语句
        int count = ExecuteSqlUtils.executeUpdate(connection, "update test_user set user_name = ?", params);

        // 执行查询的sql语句
        List<Map<String, Object>> list = ExecuteSqlUtils.executeQuery(connection, "select * from test_user where user_name = ?", params);

        // 执行删除的sql语句
        ExecuteSqlUtils.executeUpdate(connection, "delete from test_user where user_name = ?", params);

        ExecuteSqlUtils.executeUpdate(connection, "INSERT INTO test_user (user_name) VALUES (?)", params);

        connection.close();
    }
}
