package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.entity.base.AbstractDelete;
import cn.wubo.sql.forge.entity.base.AbstractInsert;
import cn.wubo.sql.forge.entity.cache.CacheService;

public class EntityDelete<T> extends AbstractDelete<T, Integer, EntityDelete<T>> {

    public EntityDelete(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Integer build(CacheService cacheService, CrudService crudService) {
        return 0;
    }


}
