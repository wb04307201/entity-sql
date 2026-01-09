package cn.wubo.sql.forge.crud.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.JDBCType;

public record Column(
        @NotBlank
        String column,
        @NotNull
        JDBCType jdbcType,
        Long length,
        Integer precision,
        Integer scale,
        boolean notnull,
        String comment
) {
}
