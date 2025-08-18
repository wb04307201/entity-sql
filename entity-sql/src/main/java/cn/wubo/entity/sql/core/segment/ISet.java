package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.functional_interface.SFunction;


public interface ISet<T,Children> {

    Children set(SFunction<T, ?> column, Object value);
}
