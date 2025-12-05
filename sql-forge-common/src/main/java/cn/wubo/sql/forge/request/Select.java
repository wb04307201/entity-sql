package cn.wubo.sql.forge.request;

import cn.wubo.sql.forge.request.base.Page;
import cn.wubo.sql.forge.request.base.Where;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Select (
        @JsonProperty("@column")
        String[] columns,
        @JsonProperty("@where")
        @Valid
        List<Where> wheres,
        @JsonProperty("@page")
        @Valid
        Page page,
        @JsonProperty("@join")
        List<String> joins,
        @JsonProperty("@order")
        String[] orders,
        @JsonProperty("@group")
        String[] groups,
        @JsonProperty("@distince")
        boolean distinct
){ }
