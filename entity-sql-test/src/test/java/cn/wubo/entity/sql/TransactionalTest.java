package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.Entity;
import cn.wubo.entity.sql.core.SQL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = {DataSourceConfig.class, EntitySqlConfiguration.class})
@Transactional
public class TransactionalTest {

    @Autowired
    private DataSourceHelper dataSourceHelper;

    @Autowired
    private TransactionalTestService transactionalTestService;


    @BeforeEach
    public void test0() {
        if (dataSourceHelper.execute(SQL.isTableExists(User.class))) {
            dataSourceHelper.execute(SQL.dropTable(User.class));
        }
        dataSourceHelper.execute(SQL.createTable(User.class));
    }

    @Test
    @Commit
    public void test1() {
        User user = new User();
        user.setUserName("99999");
        user.setBirth(LocalDate.now());
        dataSourceHelper.execute(Entity.insertOrUpdate(user));

        List<User> userList = dataSourceHelper.execute(SQL.query(User.class));
        Assertions.assertEquals(1, userList.size());
    }

    @Test
    public void test2() {
        User user = new User();
        user.setUserName("99999");
        user.setBirth(LocalDate.now());
        dataSourceHelper.execute(Entity.insertOrUpdate(user));

        List<User> userList = dataSourceHelper.execute(SQL.query(User.class));
        Assertions.assertEquals(1, userList.size());
        TestTransaction.flagForRollback();
        TestTransaction.end();

        TestTransaction.start();
        userList = dataSourceHelper.execute(SQL.query(User.class));
        Assertions.assertEquals(0, userList.size());
    }

}
