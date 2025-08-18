package cn.wubo.entity.sql.core.enums;

public enum DatabaseType {

    MY_SQL("mysql"), ORACLE("oracle"), POSTGRE_SQL("postgresql"), SQL_SERVER("sql server"), H2("h2"), SQLITE("sqlite"), DB2("db2");

    public String value;

    DatabaseType(String value) {
        this.value = value;
    }
}
