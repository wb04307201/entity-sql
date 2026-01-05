package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.crud.base.Page;
import cn.wubo.sql.forge.entity.enums.OrderType;
import cn.wubo.sql.forge.entity.inter.SFunction;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSelectPage<T, R, C extends AbstractSelectPage<T, R, C>> extends AbstractSelect<T, R, C> {
    protected Page page = new Page(0,10);

    protected AbstractSelectPage(Class<T> entityClass) {
        super(entityClass);
    }

    public C page(Integer pageIndex, Integer pageSize) {
        this.page = new Page(pageIndex, pageSize);
        return typedThis;
    }
}
