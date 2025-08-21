package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.Entity;
import cn.wubo.entity.sql.core.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TransactionalTestService {

    @Autowired
    private DataSourceHelper dataSourceHelper;

    @Transactional(rollbackFor = Exception.class)
    public void doSomethingThatShouldRollback() {
        int count = dataSourceHelper.execute(SQL.insert(User.class).set(User::getId, "22222").set(User::getUserName, "11111"));
        throw new RuntimeException("=====");
    }
}
