package cn.wubo.sql.forge.crud;

import cn.wubo.sql.forge.crud.base.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Insert(
        @JsonProperty("@set")
        @NotNull
        @Size(min = 1)
        @Valid
        List<Set> sets,
        @JsonProperty("@with_select")
        @Valid
        Select select
) {
}
