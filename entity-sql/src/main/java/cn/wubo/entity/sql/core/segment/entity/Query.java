package cn.wubo.entity.sql.core.segment.entity;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.model.ColumnModel;
import cn.wubo.entity.sql.core.segment.AbstractObject;
import cn.wubo.entity.sql.core.segment.Condition;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.DatabaseUtils;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class Query<T> extends AbstractObject<T, Query<T>, List<T>> {

    public Query(T obj, StatementType statementType) {
        super(obj, statementType);
    }

    @Override
    protected List<T> executeSql(Connection connection) {
        try {
            for (ColumnModel columnModel : tableModel.getColumns()) {
                if (columnModel.getSearch().getSearchable()) {
                    Field field = columnModel.getF();
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if(value != null) conditions.add(new Condition(columnModel.getColumn(), columnModel.getSearch().getCondition(), DatabaseUtils.transValueObj2Db(value)));
                }
            }

            selectSql();

            return ExecuteSqlUtils.executeQuery(connection, sb.toString(), params, entityClass);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException | ParseException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
