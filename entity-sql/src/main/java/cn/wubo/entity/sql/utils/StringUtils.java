package cn.wubo.entity.sql.utils;

public class StringUtils {

    /**
     * 返回字符串的默认值
     * 如果第一个字符串为null、空字符串或只包含空白字符，则返回第二个字符串作为默认值
     *
     * @param str1 待检查的字符串
     * @param str2 默认值字符串
     * @return 如果str1为空则返回str2，否则返回str1
     */
    public static String defaultValue(String str1, String str2) {
        // 如果str1为null或去除空白后为空字符串，则返回默认值str2
        if (str1 == null || str1.trim().isEmpty()) return str2;
        else return str1;
    }

    /**
     * 将驼峰命名法的字符串转换为蛇形命名法
     * 例如：camelCase -> camel_case, HelloWorld -> hello_world
     *
     * @param camelCase 驼峰命名法的字符串，可能为null或空字符串
     * @return 转换后的蛇形命名法字符串，如果输入为null或空则返回原值
     */
    public static String toSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        // 处理首字符，转换为小写
        result.append(Character.toLowerCase(camelCase.charAt(0)));

        // 遍历剩余字符，遇到大写字母则添加下划线并转换为小写
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }


    /**
     * 将下划线命名法(snake_case)转换为驼峰命名法(camelCase)
     *
     * @param snakeCase 输入的下划线命名法字符串，例如 "user_name" 或 "first_name"
     * @return 转换后的驼峰命名法字符串，例如 "userName" 或 "firstName"，
     * 如果输入为null或空字符串则直接返回原值
     */
    public static String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = snakeCase.split("_");

        // 第一个单词全小写
        if (parts.length > 0) {
            result.append(parts[0].toLowerCase());
        }

        // 后续单词首字母大写
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

        /**
     * 去除字符串中小数点后多余的零
     *
     * @param s 输入的数字字符串
     * @return 处理后的字符串，移除了小数点后多余的零，如果小数点后全为零则移除小数点
     */
    public static String subZeroAndDot(String s) {
        // 空值检查
        if (s == null) {
            return null;
        }

        // 如果字符串包含小数点，进行处理
        if (s.contains(".")) {
            // 移除小数点后所有的0
            int dotIndex = s.indexOf('.');
            String beforeDot = s.substring(0, dotIndex);
            String afterDot = s.substring(dotIndex + 1);

            // 移除小数点后尾部的0
            int endIndex = afterDot.length();
            while (endIndex > 0 && afterDot.charAt(endIndex - 1) == '0') {
                endIndex--;
            }

            // 如果小数点后全为0或者没有数字，移除小数点
            if (endIndex == 0) {
                s = beforeDot;
            } else {
                s = beforeDot + "." + afterDot.substring(0, endIndex);
            }
        }
        return s;
    }



}
