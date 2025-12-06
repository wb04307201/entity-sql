package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.entity.base.AbstractDelete;
import cn.wubo.sql.forge.entity.base.AbstractInsert;

public class EntityDelete<T> extends AbstractDelete<T, Delete, EntityDelete<T>> {

    public EntityDelete(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Delete build() {
        return null;
    }
}
