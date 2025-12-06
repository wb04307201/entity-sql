package cn.wubo.sql.forge;

import cn.wubo.sql.forge.entity.EntityDelete;
import cn.wubo.sql.forge.entity.EntityInsert;
import cn.wubo.sql.forge.entity.EntitySelect;
import cn.wubo.sql.forge.entity.EntityUpdate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Entity {

    public <T> EntityDelete<T> delete(Class<T> entityClass) {
        return new EntityDelete<>(entityClass);
    }

    public <T> EntityInsert<T> insert(Class<T> entityClass) {
        return new EntityInsert<>(entityClass);
    }

    public <T> EntitySelect<T> select(Class<T> entityClass) {
        return new EntitySelect<>(entityClass);
    }

    public <T> EntityUpdate<T> update(Class<T> entityClass) {
        return new EntityUpdate<>(entityClass);
    }
}
