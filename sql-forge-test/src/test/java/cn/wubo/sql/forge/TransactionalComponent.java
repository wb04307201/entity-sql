package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.ParamMap;
import cn.wubo.sql.forge.records.SqlScript;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;

public class TransactionalComponent {

    private final Executor executor;

    public TransactionalComponent(Executor executor) {
        this.executor = executor;
    }

    @Transactional
    void insert() throws SQLException {
        ParamMap params = new ParamMap();
        params.put("1");
        params.put("wb04307201");
        params.put("wb04307201@gitee.com");

        SqlScript sqlScript = new SqlScript("""
                INSERT INTO users (id,username,email) VALUES (?,?,?)
                """, params);

        Object key = executor.executeInsert(sqlScript);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
