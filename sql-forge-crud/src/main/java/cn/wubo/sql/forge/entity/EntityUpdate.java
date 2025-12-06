package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.entity.base.AbstractUpdate;

public class EntityUpdate<T> extends AbstractUpdate<T, Update, EntityUpdate<T>> {

    public EntityUpdate(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Update build() {
        return null;
    }
}
