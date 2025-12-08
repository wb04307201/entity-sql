package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

import java.util.ArrayList;
import java.util.List;

import static cn.wubo.sql.forge.enums.ConditionType.*;

public abstract class AbstractUpdate<T, R, C extends AbstractUpdate<T, R, C>> extends AbstractWhere<T, R, C> {
    protected List<EntitySet<T>> sets = new ArrayList<>();

    protected AbstractUpdate(Class<T> entityClass) {
        super(entityClass);
    }

    public C set(SFunction<T, ?> column, Object value) {
        sets.add(new EntitySet<>(column, value));
        return typedThis;
    }
}
