package cn.wubo.sql.forge.bulid.segment;

import cn.wubo.sql.forge.bulid.enums.ConditionType;

public record Condition(
        String column,
        ConditionType conditionType,
        Object value) {
}
