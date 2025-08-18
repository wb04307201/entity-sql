package cn.wubo.entity.sql.core.model;

import cn.wubo.entity.sql.core.enums.EditType;
import cn.wubo.entity.sql.core.enums.StatementCondition;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SearchModel {
    private Boolean searchable = false;
    private EditType type = EditType.TEXT;
    private StatementCondition condition = StatementCondition.EQ;
    private Integer searchOrder = 100;
}
