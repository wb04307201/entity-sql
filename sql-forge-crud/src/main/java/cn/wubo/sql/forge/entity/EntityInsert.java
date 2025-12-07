package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.entity.base.AbstractInsert;
import cn.wubo.sql.forge.entity.cache.CacheService;

public class EntityInsert<T> extends AbstractInsert<T, T, EntityInsert<T>> {

    public EntityInsert(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public T build(CacheService cacheService, CrudService crudService) {
        return null;
    }


}
