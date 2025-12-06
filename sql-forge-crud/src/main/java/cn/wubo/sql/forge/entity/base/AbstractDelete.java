package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

public abstract class AbstractDelete<T, R, Children extends AbstractDelete<T, R, Children>> extends AbstractWhere<T, R, Children> {
    protected SFunction<T, ?>[] columns;

    protected AbstractDelete(Class<T> entityClass) {
        super(entityClass);
    }

    public Children select(SFunction<T, ?>... columns) {
        this.columns = columns;
        return typedThis;
    }
}
