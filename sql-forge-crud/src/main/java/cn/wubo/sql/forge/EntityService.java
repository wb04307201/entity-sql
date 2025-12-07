package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.entity.base.AbstractBase;
import cn.wubo.sql.forge.entity.cache.CacheService;

import java.sql.SQLException;

public record EntityService(
        CrudService crudService,
        CacheService cacheService
) {

    public Object run(AbstractBase abstractBase) throws SQLException {
        EntityResult entityResult = abstractBase.build(cacheService);
        if (entityResult.result() instanceof Select select){
            return crudService.select(entityResult.tableName(), select);
        }
        return null;
    }
}
