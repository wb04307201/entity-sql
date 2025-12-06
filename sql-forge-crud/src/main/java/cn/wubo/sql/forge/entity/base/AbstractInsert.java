package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

import java.util.List;

public abstract class AbstractInsert<T, R, C extends AbstractInsert<T, R, C>> extends AbstractBase<T, R, C> {
    protected List<EntitySet<T>> entitySets;

    protected AbstractInsert(Class<T> entityClass) {
        super(entityClass);
    }

    public C set(SFunction<T, ?> column, Object value) {
        entitySets.add(new EntitySet<>(column, value));
        return typedThis;
    }
}
