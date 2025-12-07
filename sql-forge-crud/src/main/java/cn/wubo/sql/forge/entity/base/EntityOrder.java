package cn.wubo.sql.forge.entity.base;

import cn.wubo.sql.forge.entity.enums.OrderType;
import cn.wubo.sql.forge.entity.inter.SFunction;

public record EntityOrder<T>(
        SFunction<T, ?> colum,
        OrderType orderType
) {
}
