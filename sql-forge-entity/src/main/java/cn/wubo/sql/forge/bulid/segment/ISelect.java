package cn.wubo.sql.forge.bulid.segment;


import cn.wubo.sql.forge.inter.SFunction;

public interface ISelect<T, Children> {

    Children select(SFunction<T, ?>... columns);
}
