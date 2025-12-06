package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.entity.base.AbstractBase;
import cn.wubo.sql.forge.entity.base.AbstractInsert;

public class EntityInsert<T> extends AbstractInsert<T, Insert, EntityInsert<T>> {

    public EntityInsert(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Insert build() {
        return null;
    }
}
