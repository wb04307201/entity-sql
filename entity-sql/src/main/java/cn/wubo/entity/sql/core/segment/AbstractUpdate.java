package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.functional_interface.SFunction;

import java.sql.Connection;

public abstract class AbstractUpdate<T, Children extends AbstractUpdate<T, Children,R>,R> extends AbstractWhere<T, Children, R> implements ISet<T, Children> {

    public AbstractUpdate(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    public Children set(SFunction<T, ?> column, Object value) {
        sets.add(new Set(tableModel.getColumnByField(column), value));
        return typedThis;
    }
}
