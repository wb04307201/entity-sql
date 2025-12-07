package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.entity.base.AbstractUpdate;
import cn.wubo.sql.forge.entity.cache.CacheService;

public class EntityUpdate<T> extends AbstractUpdate<T, Integer, EntityUpdate<T>> {

    public EntityUpdate(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Integer build(CacheService cacheService, CrudService crudService) {
        return 0;
    }

}
