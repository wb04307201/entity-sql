package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;

public abstract class AbstractDelete<T, R, Children extends AbstractDelete<T, R, Children>> extends AbstractWhere<T, R, Children> {

    protected AbstractDelete(Class<T> entityClass) {
        super(entityClass);
    }
}
