package cn.wubo.entity.sql.core.segment.entity;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.model.ColumnModel;
import cn.wubo.entity.sql.core.segment.AbstractObject;
import cn.wubo.entity.sql.core.segment.Condition;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.DatabaseUtils;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteById<T> extends AbstractObject<T, DeleteById<T>, Integer> {

    public DeleteById(T obj, StatementType statementType) {
        super(obj, statementType);
    }

    @Override
    protected Integer executeSql(Connection connection) {
        try {
            for (ColumnModel columnModel : tableModel.getColumns()) {
                if (columnModel.getIsKey()) {
                    Field field = columnModel.getF();
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value != null)
                        conditions.add(new Condition(columnModel.getColumn(), columnModel.getSearch().getCondition(), DatabaseUtils.transValueObj2Db(value)));
                    else throw new EntitySqlRuntimeException("Primary key cannot be null");
                }
            }

            deleteSql();

            return ExecuteSqlUtils.executeUpdate(connection, sb.toString(), params);
        } catch (SQLException | IllegalAccessException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
