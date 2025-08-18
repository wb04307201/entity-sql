package cn.wubo.entity.sql.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于定义下拉选项或枚举项的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {

    /**
     * 选项值
     * @return 选项的值
     */
    String value();

    /**
     * 选项显示标签
     * @return 选项的显示文本
     */
    String label();
}
