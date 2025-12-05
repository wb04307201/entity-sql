package cn.wubo.sql.forge.request;

import cn.wubo.sql.forge.request.base.Set;
import cn.wubo.sql.forge.request.base.Where;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Insert(
        @JsonProperty("@set")
        @NotNull
        @Valid
        List<Set> sets,
        @JsonProperty("@with_select")
        @Valid
        Select select
) {
}
