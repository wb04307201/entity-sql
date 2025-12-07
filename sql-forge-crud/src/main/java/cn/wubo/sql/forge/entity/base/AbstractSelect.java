package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.crud.base.Page;
import cn.wubo.sql.forge.entity.inter.SFunction;

import java.util.List;

public abstract class AbstractSelect<T, R, C extends AbstractSelect<T, R, C>> extends AbstractWhere<T, R, C> {
    protected List<SFunction<T, ?>> columns;
    protected Page page;
    protected List<EntityOrder<T>> orders;

    protected AbstractSelect(Class<T> entityClass) {
        super(entityClass);
    }

    public C select(SFunction<T, ?> column) {
        this.columns.add(column);
        return typedThis;
    }

    public C select(SFunction<T, ?>... columns) {
        this.columns.addAll(List.of(columns));
        return typedThis;
    }

    public C page(Integer pageIndex, Integer pageSize) {
        this.page = new Page(pageIndex, pageSize);
        return typedThis;
    }
}
