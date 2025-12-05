package cn.wubo.sql.forge.bulid.enums;

import lombok.Getter;

@Getter
public enum ManipulationType {
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

    final String value;

    ManipulationType(String value) {
        this.value = value;
    }
}
