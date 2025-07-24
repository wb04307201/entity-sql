package cn.wubo.sql.util;

import cn.wubo.sql.util.entity.EntityUtils;
import cn.wubo.sql.util.entity.TableModel;
import cn.wubo.sql.util.enums.GenerationType;
import cn.wubo.sql.util.enums.StatementCondition;
import cn.wubo.sql.util.exception.ModelSqlException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 根据实体类生成sql
 */
public class ModelSqlUtils {

    private ModelSqlUtils() {
    }

    /**
     * 根据数据插入SQL语句，根据标识的主键{@link @Key}生成主键值
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return SQL对象
     */
    public static <T> SQL<T> insertSql(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        Class<T> clazz = (Class<T>) data.getClass();
        // 获取表信息
        TableModel tableModel = EntityUtils.getTable(clazz);
        SQL<T> sql = new SQL<T>(clazz) {
        }.insert();

        // 遍历字段列表，将非空字段添加到SQL的set语句中
        for (TableModel.ColumnModel col : tableModel.getCols()) {
            if (Boolean.TRUE.equals(col.getKey())) {
                if (col.getGenerationType() == GenerationType.UUID) {
                    sql.addSet(col.getColumnName(), UUID.randomUUID().toString());
                } else {
                    // 其他主键生成策略暂不支持，可根据需要扩展
                    throw new UnsupportedOperationException("Unsupported key generation type: " + col.getGenerationType());
                }
            } else {
                try {
                    Object valObj = EntityUtils.getValue(col.getField(), data);
                    if (valObj != null) {
                        sql.addSet(col.getColumnName(), valObj);
                    }
                } catch (IllegalAccessException e) {
                    throw new ModelSqlException(e);
                }
            }
        }

        // 解析SQL语句
        return sql;
    }

    /**
     * 生成更新SQL语句，根据标识的主键{@link @Key}更新数据
     *
     * @param data 数据对象
     * @return SQL语句
     */
    public static <T> SQL<T> updateSql(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        Class<T> clazz = (Class<T>) data.getClass();
        // 获取表信息
        TableModel tableModel = EntityUtils.getTable(clazz);
        // 初始化 SQL 更新语句构造器
        SQL<T> sql = new SQL<T>(clazz) {
        }.update();

        // 分别存储主键列和普通列
        TableModel.ColumnModel keyCol = null;
        List<TableModel.ColumnModel> columns = tableModel.getCols();

        for (TableModel.ColumnModel col : columns) {
            if (col.getKey()) {
                keyCol = col;
            } else {
                try {
                    Object valObj = EntityUtils.getValue(col.getField(), data);
                    if (valObj != null) {
                        sql.addSet(col.getColumnName(), valObj);
                    }
                } catch (IllegalAccessException e) {
                    throw new ModelSqlException(e);
                }
            }
        }

        // 校验主键是否定义
        if (keyCol == null) {
            throw new ModelSqlException("实体类 [" + clazz.getSimpleName() + "] 缺少主键字段，请使用 @Key 注解标识");
        }

        // 主键值不能为空
        try {
            Object keyValue = EntityUtils.getValue(keyCol.getField(), data);
            if (keyValue == null) {
                throw new IllegalArgumentException("实体类 [" + clazz.getSimpleName() + "] 的主键 [" + keyCol.getField().getName() + "] 值不能为空");
            }
            sql.addWhereEQ(keyCol.getColumnName(), keyValue);
        } catch (IllegalAccessException e) {
            throw new ModelSqlException(e);
        }

        return sql;
    }

    /**
     * 保存SQL语句
     *
     * @param data 保存的数据
     * @return SQL语句
     */
    public static <T> SQL<T> saveSql(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        Class<T> clazz = (Class<T>) data.getClass();
        TableModel tableModel = EntityUtils.getTable(clazz);

        // 查找主键字段且值不为null的列
        TableModel.ColumnModel keyCol = null;
        List<TableModel.ColumnModel> columns = tableModel.getCols();

        for (TableModel.ColumnModel col : columns) {
            if (col.getKey()) {
                keyCol = col;
                break;
            }
        }

        // 校验主键是否定义
        if (keyCol == null) {
            throw new ModelSqlException("实体类 [" + clazz.getSimpleName() + "] 缺少主键字段，请使用 @Key 注解标识");
        }

        // 主键值不能为空
        try {
            Object keyValue = EntityUtils.getValue(keyCol.getField(), data);
            if (keyValue == null) return insertSql(data);
            else return updateSql(data);
        } catch (IllegalAccessException e) {
            throw new ModelSqlException(e);
        }
    }


    /**
     * 构造删除SQL语句，根据标识的主键{@link @Key}删除数据
     *
     * @param data 待删除的数据对象
     * @return SQL语句
     */
    public static <T> SQL<T> deleteSql(T data) {
        Class<T> clazz = (Class<T>) data.getClass();
        // 获取表信息
        TableModel tableModel = EntityUtils.getTable(clazz);
        SQL<T> sql = new SQL<T>(clazz) {
        }.delete();

        TableModel.ColumnModel keyCol = null;
        List<TableModel.ColumnModel> columns = tableModel.getCols();

        for (TableModel.ColumnModel col : columns) {
            if (col.getKey()) {
                keyCol = col;
                break;
            }
        }

        // 校验主键是否定义
        if (keyCol == null) {
            throw new ModelSqlException("实体类 [" + clazz.getSimpleName() + "] 缺少主键字段，请使用 @Key 注解标识");
        }

        // 主键值不能为空
        try {
            Object keyValue = EntityUtils.getValue(keyCol.getField(), data);
            if (keyValue == null) {
                throw new IllegalArgumentException("实体类 [" + clazz.getSimpleName() + "] 的主键 [" + keyCol.getField().getName() + "] 值不能为空");
            }
            sql.addWhereEQ(keyCol.getColumnName(), keyValue);
        } catch (IllegalAccessException e) {
            throw new ModelSqlException(e);
        }

        return sql;
    }

    /**
     * 生成分页查询SQL
     *
     * @param data   待查询的数据
     * @param offset 分页偏移量
     * @param count  分页大小
     * @return SQL对象
     */
    public static <T> SQL<T> selectByPageSql(T data, Integer offset, Integer count) {
        Class<T> clazz = (Class<T>) data.getClass();
        // 获取表信息
        TableModel tableModel = EntityUtils.getTable(clazz);
        SQL<T> sql = new SQL<T>(clazz) {
        }.select();

        // 遍历字段列表，根据字段值生成where条件
        tableModel.getCols().forEach(col -> {
            try {
                Object valObj = EntityUtils.getValue(col.getField(), data);
                if (valObj != null) {
                    if (col.getStatementCondition() == StatementCondition.UEQ)
                        sql.addWhereUEQ(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.LIKE)
                        sql.addWhereLIKE(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.LLIKE)
                        sql.addWhereLLIKE(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.RLIKE)
                        sql.addWhereRLIKE(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.GT)
                        sql.addWhereGT(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.LT)
                        sql.addWhereLT(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.GTEQ)
                        sql.addWhereGTEQ(col.getColumnName(), valObj);
                    else if (col.getStatementCondition() == StatementCondition.LTEQ)
                        sql.addWhereLTEQ(col.getColumnName(), valObj);
                    else sql.addWhereEQ(col.getColumnName(), valObj);
                }
            } catch (IllegalAccessException e) {
                throw new ModelSqlException(e);
            }
        });

        if (offset != null && count != null) {
            sql.page(offset, count);
        }

        // 解析并返回SQL
        return sql;
    }

    /**
     * 生成选择SQL语句
     *
     * @param data 数据
     * @return SQL语句
     */
    public static <T> SQL<T> selectSql(T data) {
        return selectByPageSql(data, null, null);
    }

    /**
     * 构造函数，用于创建一个SQL对象
     *
     * @param data 数据对象
     * @return SQL对象
     */
    public static <T> SQL<T> SQL(T data) {
        return new SQL<>((Class<T>) data.getClass()) {
        };
    }

    /**
     * 根据传入的数据类型，生成对应的SQL语句
     *
     * @return 生成的SQL语句列表
     */
    public static <T> SQL<T> createSql(T data) {
        return SQL(data).create();
    }

    /**
     * 生成删除表的SQL语句
     *
     * @return 删除表的SQL语句
     */
    public static <T> SQL<T> dropSql(T data) {
        return SQL(data).drop();
    }
}
