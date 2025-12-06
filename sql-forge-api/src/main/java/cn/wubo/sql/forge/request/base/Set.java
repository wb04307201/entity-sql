package cn.wubo.sql.forge.request.base;

import jakarta.validation.constraints.NotBlank;

public record Set(
        @NotBlank
        String column,
        Object value
) { }
