package cn.wubo.sql.util;

import cn.wubo.sql.util.entity.EntityUtils;
import cn.wubo.sql.util.entity.TableModel;
import cn.wubo.sql.util.exception.ExecuteSqlException;
import cn.wubo.sql.util.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExecuteSqlUtils {

    private ExecuteSqlUtils() {
    }

    /**
     * 执行查询
     *
     * @param connection 数据库连接
     * @param sql        sql
     * @param params     参数
     * @param <T>        泛型
     * @return List<T>
     */
    public static <T> List<T> executeQuery(Connection connection, String sql, Map<Integer, Object> params, Class<T> clazz) {
        // 参数校验
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        log.debug("executeQuery ...... sql:{} paramsSize:{} class:{}", sql, params != null ? params.size() : 0, clazz.getName());

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // 参数设置
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    if (entry.getKey() != null) {
                        preparedStatement.setObject(entry.getKey(), entry.getValue());
                    }
                }
            }

            // 结果集处理
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return getResult(resultSet, clazz);
            }
        } catch (SQLException e) {
            throw new ExecuteSqlException("SQL execution failed: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ExecuteSqlException("No such method: " + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ExecuteSqlException("Instantiation failed: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ExecuteSqlException("Illegal access: " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new ExecuteSqlException("Invocation target error: " + e.getMessage(), e);
        }
    }


    /**
     * 执行查询操作并返回结果列表
     *
     * @param connection    数据库连接对象
     * @param sql           SQL查询语句
     * @param params        参数映射
     * @param typeReference 结果类型引用
     * @param <T>           结果类型
     * @return 查询结果列表
     */
    public static <T> List<T> executeQuery(Connection connection, String sql, Map<Integer, Object> params, TypeReference<T> typeReference) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null) {
            throw new IllegalArgumentException("SQL cannot be null");
        }
        if (typeReference == null) {
            throw new IllegalArgumentException("TypeReference cannot be null");
        }
        return executeQuery(connection, sql, params, typeReference.clazz);
    }


    /**
     * 执行查询操作，返回查询结果的列表
     *
     * @param connection    数据库连接对象
     * @param sql           SQL查询语句
     * @param typeReference 查询结果的类型引用
     * @return 查询结果的列表
     */
    public static <T> List<T> executeQuery(Connection connection, String sql, TypeReference<T> typeReference) {
        if (typeReference == null) {
            throw new IllegalArgumentException("typeReference cannot be null");
        }
        return executeQuery(connection, sql, new HashMap<>(), typeReference.clazz);
    }


    /**
     * 执行更新数据库操作
     *
     * @param connection 数据库连接
     * @param sql        sql
     * @param params     参数
     * @return int
     */
    public static int executeUpdate(Connection connection, String sql, Map<Integer, Object> params) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }

        log.debug("executeUpdate ...... sql:{} params:{}", sql, params);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    if (entry.getKey() != null) {
                        preparedStatement.setObject(entry.getKey(), entry.getValue());
                    }
                }
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to execute update SQL: {}", sql, e);
            throw new ExecuteSqlException("SQL execution failed: " + e.getMessage(), e);
        }
    }


    /**
     * 执行更新操作
     *
     * @param connection 数据库连接对象
     * @param sql        SQL语句
     * @return 更新的行数
     * @throws IllegalArgumentException 当connection或sql为null时抛出
     */
    public static int executeUpdate(Connection connection, String sql) {
        if (connection == null) {
            throw new IllegalArgumentException("数据库连接对象不能为null");
        }
        if (sql == null) {
            throw new IllegalArgumentException("SQL语句不能为null");
        }
        return executeUpdate(connection, sql, new HashMap<>());
    }


    /**
     * 执行多个SQL语句并返回受影响的行数
     *
     * @param connection 数据库连接对象
     * @param sqls       SQL语句列表
     * @return 受影响的行数
     */
    public static int executeUpdate(Connection connection, List<String> sqls) {
        int count = 0;
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false); // 设置自动提交为false，禁止自动提交事务
            for (String sql : sqls) {
                count += executeUpdate(connection, sql); // 执行单个SQL语句并累加受影响的行数
            }
            connection.commit(); // 提交事务
        } catch (SQLException e) {
            try {
                connection.rollback(); // 发生异常时回滚事务
            } catch (SQLException rollbackEx) {
                // 回滚失败，记录日志或处理
            }
            throw new ExecuteSqlException(e); // 抛出执行SQL异常
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit); // 恢复原始自动提交状态
            } catch (SQLException e) {
                // 状态恢复失败，记录日志或处理
            }
        }
        return count; // 返回受影响的行数
    }

    /**
     * 处理返回值
     *
     * @param rs  游标
     * @param <T> 泛型
     * @return List<T> List<T>类型的返回结果
     * @throws NoSuchMethodException     当找不到clazz类的无参构造方法时抛出该异常
     * @throws InstantiationException    当使用无参构造方法实例化clazz类失败时抛出该异常
     * @throws IllegalAccessException    当无权访问clazz类的属性或方法时抛出该异常
     * @throws SQLException              当访问数据库发生异常时抛出该异常
     * @throws InvocationTargetException 当调用方法发生异常时抛出该异常
     */
    private static <T> List<T> getResult(ResultSet rs, Class<T> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException, InvocationTargetException {
        if (clazz == null) {
            throw new IllegalArgumentException("Class type must not be null");
        }

        if (log.isDebugEnabled()) {
            log.debug("[getResult] class:{}", clazz.getName());
        }
        if (MapUtils.isMap(clazz)) return result2Map(rs, clazz);
        else return result2Class(rs, clazz);
    }

    /**
     * 将ResultSet转换为Map列表
     *
     * @param rs    ResultSet对象（调用方需负责其关闭）
     * @param clazz Map的泛型类型（必须是可实例化的具体类，如 HashMap.class）
     * @return 转换后的Map列表
     * @throws SQLException              如果出现SQL异常
     * @throws InstantiationException    如果出现实例化异常
     * @throws IllegalAccessException    如果出现访问权限异常
     * @throws NoSuchMethodException     如果出现方法不存在异常
     * @throws InvocationTargetException 如果出现方法调用目标异常
     */
    private static <T> List<T> result2Map(ResultSet rs, Class<T> clazz)
            throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!Map.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class must be a Map implementation: " + clazz.getName());
        }

        List<T> result = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // 缓存列名（1-based索引）
        String[] headers = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            headers[i - 1] = rsmd.getColumnLabel(i);
        }

        while (rs.next()) {
            T row = MapUtils.createMap(clazz);
            if (row == null) {
                throw new IllegalStateException("MapUtils.createMap returned null for class: " + clazz);
            }

            if (row instanceof Map<?, ?> map) {
                for (int i = 1; i <= columnCount; i++) {
                    ((Map<String, Object>) map).put(headers[i - 1], rs.getObject(i));
                }
            }

            result.add(row);
        }

        return result;
    }

    /**
     * 将ResultSet转换为指定类型的List集合
     *
     * @param rs    ResultSet对象
     * @param clazz 指定的类型
     * @return 转换后的List集合
     * @throws SQLException
     */
    private static <T> List<T> result2Class(ResultSet rs, Class<T> clazz) throws SQLException {
        List<T> result = new ArrayList<>();
        TableModel tableModel = EntityUtils.getTable(clazz);

        if (tableModel == null || tableModel.getCols() == null) {
            return result; // 安全返回
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // 列名 -> ColumnModel 映射
        Map<String, TableModel.ColumnModel> headerMap = new HashMap<>();
        Map<String, Integer> columnIndexMap = new HashMap<>(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            String columnName = rsmd.getColumnLabel(i);
            columnIndexMap.put(columnName, i);
            for (TableModel.ColumnModel col : tableModel.getCols()) {
                if (columnName.equalsIgnoreCase(col.getColumnName())) {
                    headerMap.put(columnName, col);
                    break;
                }
            }
        }
        while (rs.next()) {
            T row;
            try {
                row = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new ExecuteSqlException("Failed to instantiate class: " + clazz.getName(), e);
            }

            for (Map.Entry<String, TableModel.ColumnModel> entry : headerMap.entrySet()) {
                String columnName = entry.getKey();
                TableModel.ColumnModel columnModel = entry.getValue();
                Field field = columnModel.getField();

                try {
                    if (!field.trySetAccessible()) {
                        field.setAccessible(true);
                    }
                    Object value = rs.getObject(columnIndexMap.get(columnName));
                    field.set(row, EntityUtils.getValue(columnModel, value));
                } catch (IllegalAccessException | SQLException e) {
                    throw new ExecuteSqlException("Error setting field: " + field.getName(), e);
                }
            }

            result.add(row);
        }
        return result;
    }

    /**
     * 判断表是否存在
     *
     * @param connection       数据库连接
     * @param catalog          数据库名
     * @param schemaPattern    schema模式
     * @param tableNamePattern 表名
     * @param types            类型标准（数组格式），一般使用"TABLE"，即获取所有类型为TABLE的表
     * @return boolean
     */
    public static Boolean isTableExists(Connection connection, String catalog, String schemaPattern, String tableNamePattern, String[] types) {
        try (ResultSet rset = connection.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, types)) {
            return rset.next();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 判断表是否存在
     *
     * @param connection 数据库连接对象
     * @param tableName  表名
     * @return 表是否存在
     */
    public static Boolean isTableExists(Connection connection, String tableName) {
        if (connection == null) {
            return false;
        }
        if (tableName == null || tableName.isEmpty()) {
            return false;
        }
        return isTableExists(connection, null, null, tableName, new String[]{"TABLE"});
    }

}
