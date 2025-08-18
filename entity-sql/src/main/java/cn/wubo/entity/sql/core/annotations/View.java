package cn.wubo.entity.sql.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于定义实体字段在视图界面中的显示和行为配置的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface View {

    /**
     * 是否在视图中显示
     * @return true表示可显示，默认为true
     */
    boolean viewable() default true;

    /**
     * 是否支持排序
     * @return true表示支持排序，默认为true
     */
    boolean sortable() default true;

    /**
     * 是否支持导出
     * @return true表示可导出，默认为true
     */
    boolean exportable() default true;

    /**
     * 显示列的宽度
     * @return 宽度值，默认为200
     */
    int width() default 200;

    /**
     * 视图字段的显示顺序
     * @return 顺序值，默认为100
     */
    int viewOrder() default 100;
}


