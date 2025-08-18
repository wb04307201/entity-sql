package cn.wubo.entity.sql.core.segment.sql;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.AbstractBase;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateTable<T> extends AbstractBase<T, CreateTable<T>, Integer> {

    public CreateTable(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    protected Integer executeSql(Connection connection) {
        try {
            return ExecuteSqlUtils.executeUpdate(connection,sb.toString(),new HashMap<>());
        } catch (SQLException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
