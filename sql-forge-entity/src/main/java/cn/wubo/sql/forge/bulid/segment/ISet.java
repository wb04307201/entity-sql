package cn.wubo.sql.forge.bulid.segment;


public interface ISet<T, Children> {

    Children set(SFunction<T, ?> column, Object value);
}
