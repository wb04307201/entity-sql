package cn.wubo.entity.sql.core.segment.sql;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.AbstractBase;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class IsTableExists<T> extends AbstractBase<T, IsTableExists<T>, Boolean> {

    public IsTableExists(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    protected Boolean executeSql(Connection connection) {
        try {
            return ExecuteSqlUtils.isTableExists(connection, null, null, tableModel.getTableName(), new String[]{"TABLE"});
        } catch (SQLException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
