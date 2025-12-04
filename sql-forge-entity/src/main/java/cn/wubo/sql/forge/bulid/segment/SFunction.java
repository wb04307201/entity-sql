package cn.wubo.sql.forge.bulid.segment;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SFunction<T, R> extends Serializable, Function<T, R> {
}
