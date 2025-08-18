package cn.wubo.entity.sql.utils;

import cn.wubo.entity.sql.core.segment.Condition;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionUtils {

    /**
     * 编译OR条件判断，对给定条件执行多个谓词测试，只要有一个谓词返回true则整体返回true
     *
     * @param condition  待测试的条件对象，不能为null
     * @param predicates 谓词数组，用于测试条件，至少需要一个谓词，不能为null
     * @return Boolean 测试结果，只要有一个谓词返回true则返回true，否则返回false
     * @throws IllegalArgumentException 当predicates为null或长度为0时抛出
     */
    public static Boolean compileConditionOr(Condition condition, Predicate<Condition>... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException("predicate must not be null");
        }

        // 遍历所有谓词，对条件进行测试，只要有一个返回true则整体返回true
        // 如果谓词为null或执行过程中出现异常，则该谓词被视为返回false
        return Arrays.stream(predicates).anyMatch(item -> {
            if (item == null) {
                return false;
            }
            try {
                return item.test(condition);
            } catch (Exception e) {
                return false;
            }
        });
    }


    /**
     * 编译AND条件，对给定条件执行所有谓词测试
     *
     * @param condition  待测试的条件对象
     * @param predicates 谓词数组，用于测试条件
     * @return 如果所有非空谓词都返回true则返回true，否则返回false
     * @throws IllegalArgumentException 当谓词数组为null或长度为0时抛出
     */
    public static Boolean compileConditionAnd(Condition condition, Predicate<Condition>... predicates) {
        // 验证谓词数组不为空
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException("predicate must not be null");
        }

        // 对所有谓词进行测试，当所有谓词都返回true时返回true
        return Arrays.stream(predicates).allMatch(item -> {
            // 空谓词视为测试通过
            if (item == null) {
                return true;
            }
            try {
                // 执行谓词测试
                return item.test(condition);
            } catch (Exception e) {
                // 发生异常时视为测试失败
                return false;
            }
        });
    }


    /**
     * 构建条件字符串
     *
     * @param condition 条件对象，不能为空
     * @param predicate 条件判断谓词，用于验证条件是否满足，不能为空
     * @param function  条件转换函数，用于将条件对象转换为字符串，不能为空
     * @return 构建成功的条件字符串
     * @throws IllegalArgumentException 当参数为空、条件不满足或生成的字符串为空时抛出异常
     */
    public static String buildCondition(Condition condition, Predicate<Condition> predicate, Function<Condition, String> function) {
        // 参数校验
        if (condition == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate must not be null");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        // 判断条件是否满足
        if (predicate.test(condition)) {
            String result = function.apply(condition);
            if (result == null || result.isEmpty()) {
                throw new IllegalArgumentException("Generated condition string is null or empty");
            }
            return result;
        } else {
            throw new IllegalArgumentException("Received incomplete condition");
        }
    }


}
