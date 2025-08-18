package cn.wubo.entity.sql.core.functional_interface;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SFunction <T, R> extends Serializable, Function<T, R> {
}
