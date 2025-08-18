package cn.wubo.entity.sql.core.annotations;

import cn.wubo.entity.sql.core.enums.ColumnType;

import java.lang.annotation.*;

/**
 * 用于标记实体类字段与数据库列映射关系的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Column {

    /**
     * 数据库列名
     * @return 列名
     */
    String value();

    /**
     * 列的显示标签
     * @return 显示标签，默认为空字符串
     */
    String label() default "";

    /**
     * 列的数据类型
     * @return 数据类型，默认为VARCHAR
     */
    ColumnType type() default ColumnType.VARCHAR;

    /**
     * 列的长度
     * @return 长度，默认为200
     */
    int length() default 200;

    /**
     * 数值精度（用于数值类型）
     * @return 精度，默认为18
     */
    int precision() default 18;

    /**
     * 小数位数（用于数值类型）
     * @return 小数位数，默认为2
     */
    int scale() default 2;

    /**
     * 下拉选项列表
     * @return 选项数组，默认为空数组
     */
    Item[] items() default {};

    /**
     * 视图配置
     * @return 视图注解，默认为View实例
     */
    View view() default @View();

    /**
     * 编辑配置
     * @return 编辑注解，默认为Edit实例
     */
    Edit edit() default @Edit();

    /**
     * 搜索配置
     * @return 搜索注解，默认为Search实例
     */
    Search search() default @Search();
}

