package cn.wubo.sql.forge.bulid.segment;

import java.sql.Connection;

public interface IBase<T, Children, R> {

    R execute(Connection connection);
}
