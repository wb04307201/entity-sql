package cn.wubo.entity.sql.core.enums;

import lombok.Getter;

@Getter
public enum StatementType {
    DELETE("DELETE "),
    INSERT("INSERT INTO "),
    SELECT("SELECT "),
    UPDATE("UPDATE "),
    CREATE("CREATE TABLE "),
    DROP("DROP TABLE "),
    IS_TABLE_EXISTS("SHOW TABLES "),
    CREATE_TABLE("CREATE TABLE "),
    DROP_TABLE("DROP TABLE "),
    UNKNOWN("UNKNOWN ");

    String value;

    StatementType(String value) {
        this.value = value;
    }
}
