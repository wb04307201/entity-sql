package cn.wubo.entity.sql.core.annotations;

import cn.wubo.entity.sql.core.enums.GenerationType;

import java.lang.annotation.*;

/**
 * 用于标记实体类主键字段的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {

    /**
     * 是否是主键
     * @return true表示是主键，默认为false
     */
    boolean isKey() default false;

    /**
     * 主键生成策略
     * @return 生成策略类型，默认为UUID
     */
    GenerationType type() default GenerationType.UUID;
}
