package cn.wubo.sql.forge;

import lombok.Getter;

@Getter
public enum ConditionType {

    EQ(" = "),
    NOT_EQ(" <> "),
    LIKE(" LIKE "),
    NOT_LIKE(" NOT LIKE "),
    LEFT_LIKE(" LIKE "),
    RIGHT_LIKE(" LIKE "),
    GT(" > "),
    LT(" < "),
    GTEQ(" >= "),
    LTEQ(" <= "),
    BETWEEN(" BETWEEN "),
    NOT_BETWEEN(" NOT BETWEEN "),
    IN(" IN "),
    NOT_IN(" NOT IN "),
    IS_NULL(" IS NULL "),
    IS_NOT_NULL(" IS NOT NULL ");

    final String value;

    ConditionType(String value) {
        this.value = value;
    }
}

