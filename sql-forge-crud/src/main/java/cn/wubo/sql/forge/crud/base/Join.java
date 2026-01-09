package cn.wubo.sql.forge.crud.base;

import cn.wubo.sql.forge.enums.JoinType;
import jakarta.validation.constraints.NotBlank;

public record Join(
        JoinType type,
        @NotBlank
        String joinTable,
        @NotBlank
        String on
) {
}
