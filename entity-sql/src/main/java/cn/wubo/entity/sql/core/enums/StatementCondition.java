package cn.wubo.entity.sql.core.enums;

import lombok.Getter;

@Getter
public enum StatementCondition {

EQ(" = "), NOT_EQ(" <> "), LIKE(" LIKE "), NOT_LIKE(" NOT LIKE "), LEFT_LIKE(" LIKE "), RIGHT_LIKE(" LIKE "), GT(" > "), LT(" < "), GTEQ(" >= "), LTEQ(" <= "), BETWEEN(" BETWEEN "), NOT_BETWEEN(" NOT BETWEEN "), IN(" IN "), NOT_IN(" NOT IN "), IS_NUll(" IS NULL "), IS_NOT_NUll(" IS NOT NULL ");

    String value;

    StatementCondition(String value) {
        this.value = value;
    }
}
