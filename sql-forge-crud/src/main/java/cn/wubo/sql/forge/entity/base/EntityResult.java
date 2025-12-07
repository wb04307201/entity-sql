package cn.wubo.sql.forge.entity.base;

public record EntityResult<R>(
        String tableName,
        R result
) {
}
