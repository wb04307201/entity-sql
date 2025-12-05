package cn.wubo.sql.forge.request.base;

import cn.wubo.sql.forge.ConditionType;
import jakarta.validation.constraints.NotBlank;

public record Where(
        @NotBlank
        String column,
        ConditionType condition,
        Object value
) { }
