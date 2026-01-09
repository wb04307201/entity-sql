package cn.wubo.sql.forge.entity.enums;

import lombok.Getter;

@Getter
public enum OrderType {
    ASC(" asc"),
    DESC(" desc");

    final String value;

    OrderType(String value) {
        this.value = value;
    }
}
