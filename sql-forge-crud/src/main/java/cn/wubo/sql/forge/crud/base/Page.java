package cn.wubo.sql.forge.crud.base;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Page(
        @NotNull
        @Min(0)
        Integer pageIndex,
        @NotNull
        @Min(1)
        Integer pageSize
) {
}
