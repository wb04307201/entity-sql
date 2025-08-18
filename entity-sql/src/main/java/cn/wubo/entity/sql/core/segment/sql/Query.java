package cn.wubo.entity.sql.core.segment.sql;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.AbstractQuery;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class Query<T> extends AbstractQuery<T, Query<T>, List<T>> {

    public Query(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    protected List<T> executeSql(Connection connection) {
        try {
            return ExecuteSqlUtils.executeQuery(connection, sb.toString(), params, entityClass);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException | ParseException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
