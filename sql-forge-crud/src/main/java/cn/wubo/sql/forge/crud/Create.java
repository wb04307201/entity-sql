package cn.wubo.sql.forge.crud;

import cn.wubo.sql.forge.crud.base.Column;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Create(
        @JsonProperty("@column")
        @NotNull
        @Size(min = 1)
        @Valid
        List<Column> columns
) {
}
