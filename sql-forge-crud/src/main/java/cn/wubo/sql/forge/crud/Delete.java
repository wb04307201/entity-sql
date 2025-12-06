package cn.wubo.sql.forge.crud;

import cn.wubo.sql.forge.crud.base.Where;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Delete(
        @JsonProperty("@where")
        @NotNull
        @Valid
        List<Where> wheres,
        @JsonProperty("@with_select")
        @Valid
        Select select
) {
}