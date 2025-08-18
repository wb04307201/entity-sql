package cn.wubo.entity.sql.core;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.sql.*;

public class SQL {

    private SQL() {
    }

    /**
     * 创建一个用于查询指定实体类型的Query对象
     *
     * @param <T>         实体类型参数
     * @param entityClass 要查询的实体类的Class对象
     * @return 返回一个新的Query对象，用于执行SELECT类型的查询操作
     */
    public static <T> Query<T> query(Class<T> entityClass) {
        return new Query<>(entityClass, StatementType.SELECT);
    }


    /**
     * 创建一个更新操作的Update对象
     *
     * @param <T>         实体类型参数
     * @param entityClass 实体类的Class对象，用于指定要更新的实体类型
     * @return 返回一个新的Update对象，用于构建更新语句
     */
    public static <T> Update<T> update(Class<T> entityClass) {
        return new Update<>(entityClass, StatementType.UPDATE);
    }


    /**
     * 创建一个插入操作的Insert对象
     *
     * @param <T>         实体类型参数
     * @param entityClass 要插入的实体类，用于确定表结构和字段映射
     * @return 返回一个新的Insert对象，用于构建插入语句
     */
    public static <T> Insert<T> insert(Class<T> entityClass) {
        return new Insert<>(entityClass, StatementType.INSERT);
    }


    /**
     * 创建一个删除操作的Delete对象
     *
     * @param <T>         实体类型参数
     * @param entityClass 要删除的实体类，用于确定删除的目标表
     * @return 返回一个新的Delete对象，用于构建删除语句
     */
    public static <T> Delete<T> delete(Class<T> entityClass) {
        return new Delete<>(entityClass, StatementType.DELETE);
    }


    /**
     * 创建一个用于检查表是否存在的SQL语句构建器
     *
     * @param <T>         实体类类型参数
     * @param entityClass 实体类的Class对象，用于确定要检查的表名
     * @return 返回一个IsTableExists对象，用于构建检查表是否存在的SQL语句
     */
    public static <T> IsTableExists<T> isTableExists(Class<T> entityClass) {
        return new IsTableExists<>(entityClass, StatementType.IS_TABLE_EXISTS);
    }


    /**
     * 创建一个删除表的SQL语句构建器
     *
     * @param <T>         实体类型参数
     * @param entityClass 要删除表对应的实体类，用于获取表名等元信息
     * @return 返回一个新的DropTable对象，用于构建DROP TABLE语句
     */
    public static <T> DropTable<T> dropTable(Class<T> entityClass) {
        return new DropTable<>(entityClass, StatementType.DROP);
    }


    /**
     * 创建一个用于生成CREATE TABLE SQL语句的构建器对象
     *
     * @param <T>         实体类的泛型类型
     * @param entityClass 要创建表的实体类，用于解析表结构和字段信息
     * @return 返回一个新的CreateTable对象，用于构建CREATE TABLE语句
     */
    public static <T> CreateTable<T> createTable(Class<T> entityClass) {
        return new CreateTable<>(entityClass, StatementType.CREATE);
    }


}

