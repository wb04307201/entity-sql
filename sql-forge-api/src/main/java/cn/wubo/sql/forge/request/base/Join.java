package cn.wubo.sql.forge.request.base;

import cn.wubo.sql.forge.enums.JoinType;
import jakarta.validation.constraints.NotBlank;

public record Join(
        JoinType type,
        @NotBlank
        String on
) {
}
