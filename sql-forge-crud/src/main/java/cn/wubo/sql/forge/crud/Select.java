package cn.wubo.sql.forge.crud;

import cn.wubo.sql.forge.crud.base.Join;
import cn.wubo.sql.forge.crud.base.Page;
import cn.wubo.sql.forge.crud.base.Where;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

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
        @Valid
        List<Join> joins,
        @JsonProperty("@order")
        String[] orders,
        @JsonProperty("@group")
        String[] groups,
        @JsonProperty("@distince")
        boolean distinct
){ }
