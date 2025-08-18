package cn.wubo.entity.sql.utils;

import cn.wubo.entity.sql.core.functional_interface.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LambdaUtils {

    /**
     * 通过 Lambda 表达式获取对应的字段名。该方法要求传入的 Lambda 表达式必须是符合 Java 序列化规范的 getter 方法引用，
     * 如 {@code MyClass::getFieldName} 或 {@code MyClass::isFlag}。
     *
     * @param fn 表示字段 getter 方法的 Lambda 表达式，不能为空
     * @param <T> Lambda 表达式的主体类型
     * @return 对应的字段名称（首字母小写）
     * @throws IllegalArgumentException 如果 Lambda 表达式为 null、方法名不符合 getter 规范或字段名为空
     * @throws RuntimeException 如果在反射调用过程中发生错误
     */
    public static <T> String getFieldName(SFunction<T, ?> fn) {
        if (fn == null) {
            throw new IllegalArgumentException("Lambda expression cannot be null");
        }

        try {
            // 通过序列化方式获取 Lambda 表达式信息
            Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
            // 安全检查：只在必要时设置可访问性
            boolean wasAccessible = writeReplace.isAccessible();
            if (!wasAccessible) {
                writeReplace.setAccessible(true);
            }

            try {
                SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(fn);

                // 获取实现方法
                String methodName = serializedLambda.getImplMethodName();

                // 处理 getter 方法名，提取字段名
                String fieldName;
                if (methodName.startsWith("get") && methodName.length() > 3) {
                    fieldName = methodName.substring(3);
                } else if (methodName.startsWith("is") && methodName.length() > 2) {
                    fieldName = methodName.substring(2);
                } else {
                    throw new IllegalArgumentException("Invalid getter method name: " + methodName);
                }

                // 小写首字母 - 添加边界检查
                if (fieldName.isEmpty()) {
                    throw new IllegalArgumentException("Field name cannot be empty");
                }

                char firstChar = fieldName.charAt(0);
                if (Character.isLowerCase(firstChar)) {
                    return fieldName;
                } else {
                    return Character.toLowerCase(firstChar) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
                }

            } finally {
                // 恢复原始访问状态
                if (!wasAccessible) {
                    writeReplace.setAccessible(false);
                }
            }

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Required method 'writeReplace' not found in Lambda expression", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access Lambda serialization method", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error invoking Lambda serialization method", e.getCause());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Lambda expression format", e);
        }
    }

}
