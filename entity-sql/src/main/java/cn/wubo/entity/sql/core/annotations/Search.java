package cn.wubo.entity.sql.core.annotations;

import cn.wubo.entity.sql.core.enums.EditType;
import cn.wubo.entity.sql.core.enums.StatementCondition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于定义实体字段在搜索功能中的行为和显示配置的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Search {

    /**
     * 是否支持搜索
     * @return true表示支持搜索，默认为false
     */
    boolean searchable() default false;

    /**
     * 搜索控件类型
     * @return 编辑类型，默认为TEXT
     */
    EditType type() default EditType.TEXT;

    /**
     * 搜索条件类型
     * @return 条件枚举，默认为EQ(等于)
     */
    StatementCondition condition() default StatementCondition.EQ;

    /**
     * 搜索字段的显示顺序
     * @return 顺序值，默认为100
     */
    int searchOrder() default 100;
}

