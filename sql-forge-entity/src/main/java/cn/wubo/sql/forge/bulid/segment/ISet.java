package cn.wubo.sql.forge.bulid.segment;


import cn.wubo.sql.forge.inter.SFunction;

public interface ISet<T, Children> {

    Children set(SFunction<T, ?> column, Object value);
}
