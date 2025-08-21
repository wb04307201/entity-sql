package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.segment.IBase;
import cn.wubo.entity.sql.exception.EntitySqlRuntimeException;
import cn.wubo.entity.sql.utils.ExecuteSqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 多数据源连接池
 */
@Slf4j
public class DataSourceHelper {

    private DataSource dataSource;

    public DataSourceHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 在数据库连接上执行给定的函数操作
     *
     * @param <R>      函数返回值的泛型类型
     * @param function 接收数据库连接作为参数并返回结果的函数
     * @return 函数执行后的返回结果
     * @throws SQLException 当数据库操作发生错误时抛出
     */
    public <R> R run(Function<Connection, R> function) {
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            return function.apply(connection);
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        }
    }

    /**
     * 执行基础操作并返回结果
     *
     * @param <T>        泛型类型参数，表示基础类型
     * @param <Children> 泛型类型参数，表示继承自IBase的子类型
     * @param <R>        泛型类型参数，表示返回结果类型
     * @param base       基础操作接口实例，用于执行具体操作
     * @return 执行结果，类型为R
     */
    public <T, Children extends IBase<T, Children, R>, R> R execute(IBase<T, Children, R> base) {
        return run(base::execute);
    }


    /**
     * 执行SQL查询操作
     *
     * @param sql    要执行的SQL查询语句
     * @param params SQL查询参数，键为参数位置（从1开始），值为参数值
     * @return 查询结果列表，每个元素为一行数据的键值对映射
     */
    public List<Map<String, Object>> executeQuery(String sql, Map<Integer, Object> params) {
        // 获取数据库连接并执行查询操作
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            return ExecuteSqlUtils.executeQuery(connection, sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 执行SQL更新操作
     *
     * @param sql    要执行的SQL语句
     * @param params SQL语句中的参数映射，键为参数位置，值为参数值
     * @return 受影响的行数
     */
    public int executeUpdate(String sql, Map<Integer, Object> params) {
        // 获取数据库连接并执行更新操作
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            return ExecuteSqlUtils.executeUpdate(connection, sql, params);
        } catch (SQLException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }


    /**
     * 检查指定的表是否存在
     *
     * @param tableName 要检查的表名，不能为空
     * @return 如果表存在返回true，否则返回false
     * @throws EntitySqlRuntimeException 当数据库连接或查询过程中发生SQL异常时抛出
     */
    public Boolean isTableExists(String tableName) {
        // 获取数据库连接并检查表是否存在
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            return ExecuteSqlUtils.isTableExists(connection, null, null, tableName, new String[]{"TABLE"});
        } catch (SQLException e) {
            throw new EntitySqlRuntimeException(e);
        }
    }

}
