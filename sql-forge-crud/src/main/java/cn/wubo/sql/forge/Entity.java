package cn.wubo.sql.forge;

import cn.wubo.sql.forge.entity.*;
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

    public <T> EntitySave<T> save(T saveObj) {
        return new EntitySave<>((Class<T>)saveObj.getClass()).entity(saveObj);
    }

    public <T> EntityDeleteByKey<T> delete(T deleteObj) {
        return new EntityDeleteByKey<>((Class<T>)deleteObj.getClass()).entity(deleteObj);
    }

}
