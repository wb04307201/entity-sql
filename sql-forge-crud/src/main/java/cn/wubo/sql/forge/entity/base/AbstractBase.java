package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

public abstract class AbstractBase<T, R, C extends AbstractBase<T, R, C>> {

    protected final C typedThis = (C) this;

    protected final Class<T> entityClass;
    protected SFunction<T, ?>[] columns;

    protected AbstractBase(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract R build();
}
