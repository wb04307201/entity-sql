package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.Entity;
import cn.wubo.entity.sql.core.SQL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest(classes = {DataSourceConfig.class, EntitySqlConfiguration.class})
public class EntityTest {

    @Autowired
    private DataSourceHelper dataSourceHelper;

    @Test
    void test() {
        if (dataSourceHelper.execute(SQL.isTableExists(User.class))) {
            dataSourceHelper.execute(SQL.dropTable(User.class));
        }
        dataSourceHelper.execute(SQL.createTable(User.class));
        User user = new User();
        user.setUserName("99999");
        user.setBirth(LocalDate.now());
        dataSourceHelper.execute(Entity.insertOrUpdate(user));

        user.setUserName("99999+");
        dataSourceHelper.execute(Entity.insertOrUpdate(user));

        User user1 = dataSourceHelper.execute(Entity.grtById(user));
        Assertions.assertEquals(user1.getUserName(), "99999+");

        List<User> userList = dataSourceHelper.execute(Entity.query(new User()));
        Assertions.assertEquals(userList.size(), 1);

        Integer count = dataSourceHelper.execute(Entity.deleteById(user));
        Assertions.assertEquals(count, 1);

        userList = dataSourceHelper.execute(Entity.query(new User()));
        Assertions.assertEquals(userList.size(), 0);
    }
}
