package cn.wubo.entity.sql.core.segment.sql;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.AbstractUpdate;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class Update<T> extends AbstractUpdate<T, Update<T>, Integer> {

    public Update(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    protected Integer executeSql(Connection connection) {
        try {
            return ExecuteSqlUtils.executeUpdate(connection, sb.toString(), params);
        } catch (SQLException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
