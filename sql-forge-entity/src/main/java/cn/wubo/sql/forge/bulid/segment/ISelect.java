package cn.wubo.sql.forge.bulid.segment;


public interface ISelect<T, Children> {

    Children select(SFunction<T, ?>... columns);
}
