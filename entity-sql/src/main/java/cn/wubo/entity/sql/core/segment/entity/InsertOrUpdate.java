package cn.wubo.entity.sql.core.segment.entity;

import cn.wubo.entity.sql.core.enums.StatementCondition;
import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.model.ColumnModel;
import cn.wubo.entity.sql.core.segment.AbstractObject;
import cn.wubo.entity.sql.core.segment.Condition;
import cn.wubo.entity.sql.core.segment.Set;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.DatabaseUtils;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;
import cn.wubo.entity.sql.utils.TableModelUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.UUID;

public class InsertOrUpdate<T> extends AbstractObject<T, InsertOrUpdate<T>, T> {

    public InsertOrUpdate(T obj, StatementType statementType) {
        super(obj, statementType);
    }

    @Override
    protected T executeSql(Connection connection) {
        try {
            Boolean isUpdate = false;
            for (ColumnModel columnModel : tableModel.getColumns()) {
                if (columnModel.getIsKey()) {
                    Field field = columnModel.getF();
                    field.setAccessible(true);
                    String keyValue = (String) field.get(obj);
                    if (keyValue == null) {
                        keyValue = UUID.randomUUID().toString();
                        field.set(obj, keyValue);
                    } else {
                        isUpdate = true;
                        conditions.add(new Condition(columnModel.getColumn(), StatementCondition.EQ, keyValue));
                    }
                    sets.add(new Set(columnModel.getColumn(), keyValue));
                } else {
                    Field field = columnModel.getF();
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value != null)
                        sets.add(new Set(columnModel.getColumn(), DatabaseUtils.transValueObj2Db(value)));
                }
            }

            if (isUpdate) updateSql();
            else insertSql();

            ExecuteSqlUtils.executeUpdate(connection, sb.toString(), params);
            return obj;
        } catch (Exception e) {
            throw new EntitySqlRuntimeException(e);
        }
    }
}
