package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;

import java.sql.Connection;

public abstract class AbstractDelete<T, Children extends AbstractDelete<T, Children, R>, R> extends AbstractWhere<T, Children, R> {

    public AbstractDelete(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }
}
