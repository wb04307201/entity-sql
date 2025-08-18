package cn.wubo.entity.sql.core.enums;

import lombok.Getter;

@Getter
public enum EditType {

    TEXT("TEXT"), NUMBER("NUMBER"), CHECKBOX("CHECKBOX"), SELECT("SELECT"), DATE("DATE");

    String value;

    EditType(String value) {
        this.value = value;
    }
}
