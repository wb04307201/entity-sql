package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;

import java.sql.Connection;

public abstract class AbstractObject<T, Children extends AbstractObject<T, Children, R>, R> extends AbstractBase<T, Children, R> {

    protected T obj;

    public AbstractObject(T obj, StatementType statementType) {
        super((Class<T>) obj.getClass(), statementType);
        this.obj = obj;
    }

}
