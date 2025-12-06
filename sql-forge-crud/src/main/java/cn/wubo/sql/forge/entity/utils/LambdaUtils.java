package cn.wubo.sql.forge.entity.utils;

import cn.wubo.sql.forge.entity.inter.SFunction;
import lombok.experimental.UtilityClass;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class LambdaUtils {

    public <T> String getFieldName(SFunction<T, ?> fn) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
        SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(fn);
        String fieldName = getFieldName(serializedLambda);

        char firstChar = fieldName.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return fieldName;
        } else {
            return Character.toLowerCase(firstChar) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
        }
    }

    private String getFieldName(SerializedLambda serializedLambda) {
        String methodName = serializedLambda.getImplMethodName();
        String fieldName;
        if (methodName.startsWith("get") && methodName.length() > 3) {
            fieldName = methodName.substring(3);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            fieldName = methodName.substring(2);
        } else {
            throw new IllegalArgumentException("Invalid getter method name: " + methodName);
        }
        return fieldName;
    }
}
