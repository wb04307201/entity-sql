package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Condition {
    private String column;
    private StatementCondition statementCondition;
    private Object value;
}
