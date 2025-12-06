package cn.wubo.sql.forge.crud.base;

import jakarta.validation.constraints.NotBlank;

public record Set(
        @NotBlank
        String column,
        Object value
) { }
