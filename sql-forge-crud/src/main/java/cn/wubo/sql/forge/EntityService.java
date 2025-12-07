package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.entity.base.AbstractBase;
import cn.wubo.sql.forge.entity.base.EntityResult;

import java.sql.SQLException;

public record EntityService(CrudService crudService) {

    public Object run(AbstractBase abstractBase) throws SQLException {
        EntityResult entityResult = abstractBase.build();
        if (entityResult.result() instanceof Select select){
            return crudService.select(entityResult.tableName(), select);
        }
        return null;
    }
}
