package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.inter.SFunction;
import cn.wubo.sql.forge.enums.ConditionType;

public record EntityCondition<T>(
        SFunction<T, ?> column,
        ConditionType condition,
        Object value
) {
}
