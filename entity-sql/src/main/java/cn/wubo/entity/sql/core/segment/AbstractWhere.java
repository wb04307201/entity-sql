package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.functional_interface.SFunction;

import java.util.List;

import static cn.wubo.entity.sql.core.enums.StatementCondition.*;

public abstract class AbstractWhere<T, Children extends AbstractWhere<T, Children, R>, R> extends AbstractBase<T, Children, R> implements IWhere<T, Children> {

    public AbstractWhere(Class<T> entityClass, StatementType statementType) {
        super(entityClass, statementType);
    }

    @Override
    public Children eq(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), EQ, value));
        return typedThis;
    }

    @Override
    public Children neq(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), NOT_EQ, value));
        return typedThis;
    }

    @Override
    public Children gt(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), GT, value));
        return typedThis;
    }

    @Override
    public Children ge(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), GTEQ, value));
        return typedThis;
    }

    @Override
    public Children lt(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), LT, value));
        return typedThis;
    }

    @Override
    public Children le(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), LTEQ, value));
        return typedThis;
    }

    @Override
    public Children like(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), LIKE, value));
        return typedThis;
    }

    @Override
    public Children notLike(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), NOT_LIKE, value));
        return typedThis;
    }

    @Override
    public Children llike(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), LEFT_LIKE, value));
        return typedThis;
    }

    @Override
    public Children rlike(SFunction<T, ?> column, Object value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), RIGHT_LIKE, value));
        return typedThis;
    }

    @Override
    public Children between(SFunction<T, ?> column, Object value1, Object value2) {
        conditions.add(new Condition(tableModel.getColumnByField(column), BETWEEN, List.of(value1, value2)));
        return typedThis;
    }

    @Override
    public Children notBetween(SFunction<T, ?> column, Object value1, Object value2) {
        conditions.add(new Condition(tableModel.getColumnByField(column), NOT_BETWEEN, List.of(value1, value2)));
        return typedThis;
    }

    @Override
    public Children in(SFunction<T, ?> column, List<Object> value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), IN, value));
        return typedThis;
    }

    @Override
    public Children notIn(SFunction<T, ?> column, List<Object> value) {
        conditions.add(new Condition(tableModel.getColumnByField(column), NOT_IN, value));
        return typedThis;
    }

    @Override
    public Children isNull(SFunction<T, ?> column) {
        conditions.add(new Condition(tableModel.getColumnByField(column), IS_NUll, null));
        return typedThis;
    }

    @Override
    public Children isNotNull(SFunction<T, ?> column) {
        conditions.add(new Condition(tableModel.getColumnByField(column), IS_NOT_NUll, null));
        return typedThis;
    }
}
