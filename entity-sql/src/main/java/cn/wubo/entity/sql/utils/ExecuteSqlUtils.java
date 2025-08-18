package cn.wubo.entity.sql.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExecuteSqlUtils {

    private ExecuteSqlUtils() {
    }

    /**
     * 执行SQL查询并返回结果列表
     *
     * @param connection 数据库连接对象，不能为空
     * @param sql        要执行的SQL查询语句，不能为空或空字符串
     * @param params     SQL参数映射，键为参数位置，值为参数值，可为空
     * @return 查询结果列表，每个元素为一行数据的键值对映射
     * @throws SQLException 当数据库操作发生错误时抛出
     */
    public static List<Map<String, Object>> executeQuery(Connection connection, String sql, Map<Integer, Object> params) throws SQLException {
        // 参数校验
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }

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
                return resultSetToList(resultSet);
            }
        }
    }

    /**
     * 执行查询SQL语句并返回指定类型的对象列表
     *
     * @param connection  数据库连接对象
     * @param sql         查询SQL语句
     * @param params      SQL参数映射，键为参数位置，值为参数值
     * @param entityClass 要转换成的实体类类型
     * @return 查询结果转换成的指定类型对象列表
     * @throws SQLException              SQL执行异常
     * @throws ParseException            数据解析异常
     * @throws InvocationTargetException 反射调用异常
     * @throws NoSuchMethodException     方法不存在异常
     * @throws InstantiationException    实例化异常
     * @throws IllegalAccessException    非法访问异常
     */
    public static <T> List<T> executeQuery(Connection connection, String sql, Map<Integer, Object> params, Class<T> entityClass) throws SQLException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 执行SQL查询，获取结果集
        List<Map<String, Object>> list = ExecuteSqlUtils.executeQuery(connection, sql, params);
        List<T> result = new ArrayList<>();
        // 遍历查询结果，将每行数据转换为指定类型的对象
        for (Map<String, Object> map : list) result.add(TableModelUtils.convertToObject(map, entityClass));
        return result;
    }


    /**
     * 将ResultSet结果集转换为List<Map<String, Object>>格式
     *
     * @param rs ResultSet结果集对象
     * @return 包含查询结果的List，每个元素是一个Map，key为列名，value为对应值
     * @throws SQLException 当操作ResultSet出现SQL异常时抛出
     */
    private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 预估容量避免HashMap频繁扩容
        int initialCapacity = columnCount > 0 ? (int) (columnCount / 0.75f) + 1 : 16;

        // 遍历ResultSet中的每一行数据
        while (rs.next()) {
            Map<String, Object> rowMap = new HashMap<>(initialCapacity);
            // 遍历当前行的每一列，将列名和值存入Map
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                rowMap.put(StringUtils.toCamelCase(columnName), value);
            }
            resultList.add(rowMap);
        }

        return resultList;
    }


    /**
     * 执行更新数据库操作
     *
     * @param connection 数据库连接
     * @param sql        sql
     * @param params     参数
     * @return int
     */
    public static int executeUpdate(Connection connection, String sql, Map<Integer, Object> params) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }

        log.debug("executeUpdate ...... sql:{}", sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    Integer index = entry.getKey();
                    Object value = entry.getValue();

                    // 验证参数索引有效性
                    if (index != null && index > 0) {
                        preparedStatement.setObject(index, value);
                    } else if (index != null && index <= 0) {
                        throw new IllegalArgumentException("Parameter index must be greater than 0, got: " + index);
                    }
                }
            }
            return preparedStatement.executeUpdate();
        }
    }


    /**
     * 检查指定的表是否存在
     *
     * @param connection       数据库连接对象，不能为null
     * @param catalog          数据库目录名称，可为null
     * @param schemaPattern    模式名称模式，可为null
     * @param tableNamePattern 表名模式，不能为null或空字符串
     * @param types            表类型数组，可为null
     * @return 如果表存在返回true，否则返回false
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    public static Boolean isTableExists(Connection connection, String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (tableNamePattern == null || tableNamePattern.isEmpty()) {
            throw new IllegalArgumentException("tableNamePattern cannot be null or empty");
        }

        DatabaseMetaData metaData = connection.getMetaData();

        // 检查标识符存储规则
        boolean storesUpper = metaData.storesUpperCaseIdentifiers();
        boolean storesLower = metaData.storesLowerCaseIdentifiers();

        if (storesUpper) {
            tableNamePattern = tableNamePattern.toUpperCase(); // 转为大写
        } else if (storesLower) {
            tableNamePattern = tableNamePattern.toLowerCase(); // 转为小写
        }

        // 通过数据库元数据查询表信息
        try (ResultSet rset = metaData.getTables(catalog, schemaPattern, tableNamePattern, types)) {
            return rset.next();
        }
    }

}
