package cn.wubo.entity.sql.core.annotations;

import cn.wubo.entity.sql.core.enums.GenerationType;

import java.lang.annotation.*;

/**
 * 用于标记实体类主键字段的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Key {

    /**
     * 主键生成策略
     * @return 生成策略类型，默认为UUID
     */
    GenerationType value() default GenerationType.UUID;
}
