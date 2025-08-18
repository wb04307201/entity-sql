package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.functional_interface.SFunction;
import cn.wubo.entity.sql.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class AbstractPage<T, Children extends AbstractPage<T, Children, R>, R> extends AbstractWhere<T, Children, R> implements IPage<Children> {

    public AbstractPage(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    public Children page(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        return typedThis;
    }
}
