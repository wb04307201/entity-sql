package cn.wubo.sql.forge.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.sql.Types;

@UtilityClass
public class ReflectUtils {

    public String getJavaSqlTypeName(int dataType) {
        Field[] fields = Types.class.getFields();
        for (Field field : fields) {
            try {
                if (field.getType() == int.class && field.getInt(null) == dataType) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                // 忽略异常
            }
        }
        return "UNKNOWN(" + dataType + ")";
    }
}
