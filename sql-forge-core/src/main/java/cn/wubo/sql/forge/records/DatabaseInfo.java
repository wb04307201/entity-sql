package cn.wubo.sql.forge.records;

import cn.wubo.sql.forge.enums.TableType;

import java.util.List;

/**
 * 数据库信息记录类
 */
public record DatabaseInfo(
        String productName,
        String productVersion,
        String url,
        String userName,
        String driverName,
        String driverVersion,
        List<TableType> tableTypes
) {
}