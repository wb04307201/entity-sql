package cn.wubo.entity.sql.core.annotations;

import java.lang.annotation.*;

/**
 * 用于标记实体类对应的数据库表名的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Table {

    /**
     * 数据库表名
     * @return 表名
     */
    String value();

    /**
     * 是否初始化表结构
     * @return true表示需要初始化表结构，默认为false
     */
    boolean init() default false;
}

