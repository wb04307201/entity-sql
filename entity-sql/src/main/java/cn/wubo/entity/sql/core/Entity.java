package cn.wubo.entity.sql.core;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.segment.entity.DeleteById;
import cn.wubo.entity.sql.core.segment.entity.GetById;
import cn.wubo.entity.sql.core.segment.entity.InsertOrUpdate;
import cn.wubo.entity.sql.core.segment.entity.Query;

public class Entity {

    /**
     * 创建一个插入或更新操作的构建器对象
     *
     * @param <T>    实体类型泛型参数
     * @param entity 要进行插入或更新操作的实体对象
     * @return 返回一个新的InsertOrUpdate构建器实例，用于后续的数据库操作配置
     */
    public static <T> InsertOrUpdate<T> insertOrUpdate(T entity) {
        return new InsertOrUpdate<>(entity, StatementType.UNKNOWN);
    }


    /**
     * 创建一个新的查询对象
     *
     * @param <T>    实体类型参数
     * @param entity 查询的实体对象
     * @return 返回一个新的Query对象，包含指定的实体和未知的语句类型
     */
    public static <T> Query<T> query(T entity) {
        return new Query<>(entity, StatementType.UNKNOWN);
    }


    /**
     * 根据实体对象创建GetById查询对象
     *
     * @param <T>    实体类型泛型参数
     * @param entity 要查询的实体对象
     * @return 返回一个新的GetById查询对象，包含指定的实体和未知类型的SQL语句
     */
    public static <T> GetById<T> grtById(T entity) {
        return new GetById<>(entity, StatementType.UNKNOWN);
    }


    /**
     * 创建一个根据ID删除实体的删除操作对象
     *
     * @param <T>    实体类型泛型参数
     * @param entity 要删除的实体对象，用于确定实体类型和获取ID信息
     * @return DeleteById<T> 删除操作对象，用于执行根据ID删除实体的操作
     */
    public static <T> DeleteById<T> deleteById(T entity) {
        return new DeleteById<>(entity, StatementType.UNKNOWN);
    }

}