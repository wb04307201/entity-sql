package cn.wubo.entity.sql.core.annotations;

import cn.wubo.entity.sql.core.enums.EditType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于定义实体字段在编辑界面中的显示和行为配置的注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Edit {

    /**
     * 是否可编辑
     * @return true表示可编辑，默认为true
     */
    boolean editable() default true;

    /**
     * 编辑控件类型
     * @return 编辑类型，默认为TEXT
     */
    EditType type() default EditType.TEXT;

    /**
     * 是否为必填项
     * @return true表示必填，默认为false
     */
    boolean required() default false;

    /**
     * 编辑字段的显示顺序
     * @return 顺序值，默认为100
     */
    int editOrder() default 100;

    /**
     * 输入框提示信息
     * @return 提示文本，默认为空字符串
     */
    String placeholder() default "";
}

