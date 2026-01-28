package cn.wubo.sql.forge.inter;


import cn.wubo.sql.forge.crud.IAllowedRecord;

public interface IExecute<T extends IAllowedRecord> {

    T before(String tableName, T t);
}
