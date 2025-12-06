package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

public abstract class AbstractSelect<T, R, C extends AbstractSelect<T, R, C>> extends AbstractWhere<T, R, C> {
    protected SFunction<T, ?>[] columns;

    protected AbstractSelect(Class<T> entityClass) {
        super(entityClass);
    }

    public C select(SFunction<T, ?>... columns) {
        this.columns = columns;
        return typedThis;
    }
}
