package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.entity.base.AbstractSelect;

public class EntitySelect<T> extends AbstractSelect<T, Select, EntitySelect<T>> {

    public EntitySelect(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Select build() {
        return null;
    }
}
