package cn.wubo.sql.util;

import cn.wubo.sql.util.exception.ConnectionPoolException;
import cn.wubo.sql.util.utils.ArgUtils;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static com.alibaba.druid.pool.DruidAbstractDataSource.*;

/**
 * 多数据源连接池
 */
@Slf4j
public class MutilConnectionPool {
    private static Integer initialSize = DEFAULT_INITIAL_SIZE;
    private static Integer maxActive = DEFAULT_MAX_ACTIVE_SIZE;
    private static Integer minIdle = DEFAULT_MIN_IDLE;
    private static Integer maxWait = DEFAULT_MAX_WAIT;
    private static Integer connectionErrorRetryAttempts = 1;
    private static Boolean breakAfterAcquireFailure = Boolean.FALSE;

    /**
     * 设置默认的初始大小
     *
     * @param initialSize 默认的初始大小
     */
    public static void setDefaultInitialSize(Integer initialSize) {
        MutilConnectionPool.initialSize = initialSize;
    }

    /**
     * 设置默认的最大连接数
     *
     * @param maxActive 最大连接数
     */
    public static void setDefaultMaxActive(Integer maxActive) {
        MutilConnectionPool.maxActive = maxActive;
    }

    /**
     * 设置默认的最小空闲连接数
     *
     * @param minIdle 最小空闲连接数
     */
    public static void setDefaultMinIdle(Integer minIdle) {
        MutilConnectionPool.minIdle = minIdle;
    }

    /**
     * 设置默认的最大等待时间
     *
     * @param maxWait 最大等待时间，以秒为单位
     */
    public static void setDefaultMaxWait(Integer maxWait) {
        MutilConnectionPool.maxWait = maxWait;
    }

    /**
     * 设置默认的连接错误重试次数
     *
     * @param connectionErrorRetryAttempts 连接错误重试次数
     */
    public static void setDefaultConnectionErrorRetryAttempts(Integer connectionErrorRetryAttempts) {
        MutilConnectionPool.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
    }

    /**
     * 设置获取连接失败后是否中断连接池的使用
     *
     * @param breakAfterAcquireFailure 获取连接失败后是否中断连接池的使用
     */
    public static void setDefaultBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
        MutilConnectionPool.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    private MutilConnectionPool() {
    }

    private static ConcurrentMap<String, DataSource> poolMap = new ConcurrentHashMap<>();

    /**
     * 检查key是否存在于缓存池中
     *
     * @param key 缓存池的key
     * @return true表示存在，false表示不存在
     */
    public static synchronized Boolean check(String key) {
        // 检查key是否为空
        ArgUtils.isEmpty("key", key);
        // 检查key是否存在于缓存池中
        return poolMap.containsKey(key);
    }

    /**
     * 初始化数据源
     *
     * @param key      数据源的唯一标识
     * @param url      数据库URL
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public static synchronized void init(String key, String url, String username, String password) {
        // 参数有效性检查
        ArgUtils.isEmpty("key", key);
        // 参数有效性检查
        ArgUtils.isEmpty("url, username", url, username);
        // 创建DruidDataSource对象
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url); // 设置数据库URL
        druidDataSource.setUsername(username); // 设置用户名
        druidDataSource.setPassword(Objects.requireNonNull(password, "参数password不能为空！")); // 设置密码
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts); // 设置连接错误重试次数
        druidDataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        // 将DruidDataSource对象存入map中
        poolMap.putIfAbsent(key, druidDataSource);
    }

    /**
 * 初始化连接池
 *
 * @param key        连接池的key
 * @param datasource 数据源
 */
public static void init(String key, DataSource datasource) {
    ArgUtils.isEmpty("key", key);
    // 将数据源存入map中
    poolMap.putIfAbsent(key, datasource);
}


        /**
     * 获取连接
     *
     * @param key 连接池的键
     * @return 数据库连接
     */
    public static Connection getConnection(String key) {
        // 检查参数是否为空
        ArgUtils.isEmpty("key", key);

        // 检查连接池是否已初始化
        DataSource ds = poolMap.get(key);
        if (ds != null) {
            try {
                // 获取连接
                return ds.getConnection();
            } catch (SQLException e) {
                // 抛出异常
                throw new ConnectionPoolException("获取数据库连接失败", e);
            }
        } else {
            // 抛出异常
            throw new ConnectionPoolException("数据源还未初始化");
        }
    }


    /**
     * 根据给定的键移除连接池中的连接
     *
     * @param key 键值，用于标识特定的连接池。此键用于在连接池映射中定位到特定的连接池资源。
     * @throws ConnectionPoolException 如果关闭连接池时发生异常，会抛出此异常。
     */
    public static void remove(String key) {
        DataSource ds = null;
        synchronized (poolMap) {
            // 检查连接池映射中是否存在指定的键
            if (poolMap.containsKey(key)) {
                // 从映射中获取到对应的连接池资源
                ds = poolMap.get(key);
                // 从连接池映射中移除指定的键值对，完成连接池的移除操作。
                poolMap.remove(key);
            }
        }

        if (ds != null) {
            // 检查DataSource是否实现了AutoCloseable接口，以支持资源的自动释放
            if (ds instanceof AutoCloseable) {
                AutoCloseable ac = (AutoCloseable) ds;
                try {
                    // 安全关闭连接池，释放资源。这是移除连接池前的重要步骤。
                    ac.close();
                } catch (Exception e) {
                    // 在关闭资源时遇到异常，封装并抛出，以便上层处理。
                    throw new ConnectionPoolException(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * 清空连接池中的所有连接。
     * 此方法将遍历连接池中的所有数据源，并尝试关闭它们。
     * 完成后，会清空连接池中的所有条目。
     *
     * @无参数
     * @无返回值
     */
    public static synchronized void clear() {
        // 记录清理过程中发生的异常
        Exception lastException = null;

        // 遍历连接池中的所有键值对，尝试关闭每个数据源
        for (Map.Entry<String, DataSource> entry : poolMap.entrySet()) {
            // 关闭连接池
            if (entry.getValue() instanceof AutoCloseable ac) {
                try {
                    ac.close();
                } catch (Exception e) {
                    // 记录异常但继续处理其他连接
                    lastException = e;
                }
            }
        }

        // 清空连接池，准备接收新的连接
        poolMap.clear();

        // 如果有异常发生，抛出连接池异常
        if (lastException != null) {
            throw new ConnectionPoolException("清理连接池时发生异常", lastException);
        }
    }


    /**
     * 在数据库连接上执行Function函数
     *
     * @param key      数据库连接的key
     * @param function 数据库连接和返回值类型的Function函数
     * @param <R>      返回值类型
     * @return 执行结果
     * @throws ConnectionPoolException 获取数据库连接失败时抛出的异常
     */
    public static <R> R run(String key, Function<Connection, R> function) {
        try (Connection connection = getConnection(key)) {
            // 在数据库连接上执行Function函数
            return function.apply(connection);
        } catch (SQLException e) {
            // 如果获取数据库连接失败，则抛出ConnectionPoolException异常
            throw new ConnectionPoolException(e);
        }
    }
}
