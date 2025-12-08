package cn.wubo.sql.forge;

import cn.wubo.sql.forge.entity.EntityInsert;
import cn.wubo.sql.forge.entity.base.AbstractBase;
import cn.wubo.sql.forge.entity.cache.CacheService;

public record EntityService(
        CrudService crudService,
        CacheService cacheService
) {

    public <T,R,C extends AbstractBase<T,R,C>> R run(AbstractBase<T,R,C> abstractBase) throws Exception {
        return abstractBase.run(cacheService, crudService);
    }
}
