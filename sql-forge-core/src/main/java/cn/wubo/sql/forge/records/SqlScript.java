package cn.wubo.sql.forge.records;

import cn.wubo.sql.forge.map.ParamMap;
import jakarta.validation.constraints.NotBlank;

public record SqlScript(
        @NotBlank
        String sql,
        ParamMap params
) {
}
