package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.functional_interface.SFunction;

public interface ISelect<T,Children> {

    Children select(SFunction<T, ?>... columns);
}
