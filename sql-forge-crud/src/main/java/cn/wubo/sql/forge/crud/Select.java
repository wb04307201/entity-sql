package cn.wubo.sql.forge.crud;

import cn.wubo.sql.forge.crud.base.Join;
import cn.wubo.sql.forge.crud.base.Page;
import cn.wubo.sql.forge.crud.base.Where;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public record Select(
        @JsonProperty("@column")
        List<String> columns,
        @JsonProperty("@where")
        @Valid
        List<Where> wheres,
        @JsonProperty("@join")
        @Valid
        List<Join> joins,
        @JsonProperty("@order")
        List<String> orders,
        @JsonProperty("@group")
        List<String> groups,
        @JsonProperty("@distince")
        boolean distinct
) {
}
