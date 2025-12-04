package cn.wubo.sql.forge.bulid.segment;


import java.util.List;

public interface IWhere<T, Children> {

    Children eq(SFunction<T, ?> column, Object value);

    Children neq(SFunction<T, ?> column, Object value);

    Children gt(SFunction<T, ?> column, Object value);

    Children ge(SFunction<T, ?> column, Object value);

    Children lt(SFunction<T, ?> column, Object value);

    Children le(SFunction<T, ?> column, Object value);

    Children like(SFunction<T, ?> column, Object value);

    Children notLike(SFunction<T, ?> column, Object value);

    Children llike(SFunction<T, ?> column, Object value);

    Children rlike(SFunction<T, ?> column, Object value);

    Children between(SFunction<T, ?> column, Object value1, Object value2);

    Children notBetween(SFunction<T, ?> column, Object value1, Object value2);

    default Children in(SFunction<T, ?> column, Object... value) {
        return in(column, List.of(value));
    }

    Children in(SFunction<T, ?> column, List<Object> value);

    default Children notIn(SFunction<T, ?> column, Object... value) {
        return notIn(column, List.of(value));
    }

    Children notIn(SFunction<T, ?> column, List<Object> value);

    Children isNull(SFunction<T, ?> column);

    Children isNotNull(SFunction<T, ?> column);

}
