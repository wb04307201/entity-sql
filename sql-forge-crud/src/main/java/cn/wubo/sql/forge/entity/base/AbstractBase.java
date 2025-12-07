package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.inter.SFunction;

public abstract class AbstractBase<T, R, C extends AbstractBase<T, R, C>> {

    protected final C typedThis = (C) this;

    protected Class<T> entityClass;
    protected SFunction<T, ?>[] columns;

    protected AbstractBase(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract R build(CacheService cacheService, CrudService crudService);
}
