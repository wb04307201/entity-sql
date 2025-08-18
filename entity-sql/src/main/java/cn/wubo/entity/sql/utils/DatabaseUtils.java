package cn.wubo.entity.sql.utils;

import cn.wubo.entity.sql.core.model.ColumnModel;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatabaseUtils {

    private DatabaseUtils() {
    }


    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 根据数据库连接获取对应的数据库驱动类型
     *
     * @param connection 数据库连接对象，用于获取数据库元数据
     * @return DatabaseDriver 数据库驱动类型枚举值
     * @throws SQLException 当获取数据库元数据或产品名称失败时抛出
     */
    public static DatabaseDriver getDatabaseDriver(Connection connection) throws SQLException {
        // 通过连接的元数据获取数据库产品名称，并转换为对应的数据库驱动类型
        return DatabaseDriver.fromProductName(connection.getMetaData().getDatabaseProductName());
    }


    /**
     * 将数据库中的值转换为对象字段所需的目标类型值
     *
     * @param col   数据库列模型，包含目标字段的类型信息
     * @param value 来自数据库的原始值
     * @return 转换后的目标类型值
     * @throws ParseException 当日期格式转换失败时抛出此异常
     */
    public static Object transValueDb2Obj(ColumnModel col, Object value) throws ParseException {
        if (value == null) return value;

        Class<?> targetType = col.getF().getType();
        Class<?> sourceType = value.getClass();

        // 如果目标类型与源类型相同，直接返回原值
        if (targetType == sourceType) {
            return value;
        }

        // 根据目标类型进行相应的类型转换
        if (targetType == Integer.class) {
            String cleaned = StringUtils.subZeroAndDot(value.toString());
            return Integer.valueOf(cleaned);
        } else if (targetType == Long.class) {
            String cleaned = StringUtils.subZeroAndDot(value.toString());
            return Long.valueOf(cleaned);
        } else if (targetType == Double.class) {
            return Double.valueOf(value.toString());
        } else if (targetType == Float.class) {
            return Float.valueOf(value.toString());
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else if (targetType == Date.class || targetType == java.sql.Date.class) {
            return Date.from(java.time.LocalDate.parse(value.toString(), DATE_FORMATTER).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        } else if (targetType == LocalDate.class) {
            return LocalDate.parse(value.toString(), DATE_FORMATTER);
        } else if (targetType == Timestamp.class) {
            return Timestamp.from(LocalDateTime.parse(value.toString(), TIMESTAMP_FORMATTER).atZone(java.time.ZoneId.systemDefault()).toInstant());
        } else {
            return value;
        }
    }


    /**
     * 将值对象转换为数据库存储格式
     *
     * @param value 需要转换的值对象
     * @return 转换后的数据库存储格式值，如果输入为null则返回null
     */
    public static Object transValueObj2Db(Object value) {
        if (value == null) return value;

        // 字符串类型直接返回
        if (value instanceof String) {
            return value;
            // 时间戳类型转换为格式化字符串
        } else if (value instanceof Timestamp || value instanceof java.sql.Timestamp) {
            return ((Timestamp) value).toLocalDateTime().format(TIMESTAMP_FORMATTER);
            // 日期类型转换为格式化字符串
        } else if (value instanceof Date || value instanceof java.sql.Date) {
            return ((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
            // 本地日期类型转换为格式化字符串
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DATE_FORMATTER);
            // 浮点数类型去除末尾的0和小数点
        } else if (value instanceof Float || value instanceof Double) {
            return StringUtils.subZeroAndDot(String.valueOf(value));
            // 其他类型直接返回
        } else {
            return value;
        }
    }

}
