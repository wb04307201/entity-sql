package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;
import cn.wubo.sql.forge.enums.ConditionType;

public record EntitySet<T>(
        SFunction<T, ?> column,
        Object value
) {
}
