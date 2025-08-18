package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.SQL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest(classes = {DataSourceConfig.class, EntitySqlConfiguration.class})
public class SQLTest {

    @Autowired
    private DataSourceHelper dataSourceHelper;

    @Test
    void test() throws SQLException {
        if (dataSourceHelper.execute(SQL.isTableExists(User.class))) {
            dataSourceHelper.execute(SQL.dropTable(User.class));
        }
        dataSourceHelper.execute(SQL.createTable(User.class));

        int count = dataSourceHelper.execute(SQL.insert(User.class).set(User::getId, "11111").set(User::getUserName, "11111"));
        Assertions.assertEquals(count, 1);

        count = dataSourceHelper.execute(SQL.update(User.class).set(User::getUserName, "11111+++").eq(User::getId, "11111"));
        Assertions.assertEquals(count, 1);

        IntStream.range(0, 10).forEach(i -> {
            dataSourceHelper.execute(SQL.insert(User.class).set(User::getId, UUID.randomUUID()).set(User::getUserName, UUID.randomUUID()));
        });

        List<User> userList = dataSourceHelper.execute(SQL.query(User.class));
        Assertions.assertEquals(userList.size(), 11);

        count = dataSourceHelper.execute(SQL.delete(User.class).eq(User::getId, "11111"));

        userList = dataSourceHelper.execute(SQL.query(User.class));
        Assertions.assertEquals(userList.size(), 10);

        count = dataSourceHelper.execute(SQL.delete(User.class).isNotNull(User::getUserName));
        Assertions.assertEquals(count, 10);

        userList = dataSourceHelper.execute(SQL.query(User.class).page(0,1));
        Assertions.assertEquals(userList.size(), 1);
    }
}
