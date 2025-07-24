package cn.wubo.sql.util.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class MapUtils {

    private static final String MAP_NAME = "java.util.Map";

    private MapUtils() {
    }

    /**
     * 判断给定的类是否是Map接口或其实现类
     *
     * @param clazz 给定的类
     * @return 如果是Map接口或其实现类则返回true，否则返回false
     */
    public static Boolean isMap(Class<?> clazz) {
        // 空值检查
        if (clazz == null) {
            return false;
        }

        // 使用isAssignableFrom进行类型检查，更安全可靠
        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * 根据给定的类类型创建并返回一个空的Map对象。
     *
     * @param clazz Map对象的类类型
     * @return 创建的空的Map对象
     * @throws InstantiationException 当实例化创建Map对象时发生异常
     * @throws IllegalAccessException 当访问实例化创建的Map对象时发生异常
     */
    public static <T> T createMap(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }

        Map<Class<?>, Supplier<Object>> mapSuppliers = new HashMap<>();

        mapSuppliers.put(Properties.class, Properties::new);
        mapSuppliers.put(Hashtable.class, Hashtable::new);
        mapSuppliers.put(IdentityHashMap.class, IdentityHashMap::new);
        mapSuppliers.put(TreeMap.class, TreeMap::new);
        mapSuppliers.put(SortedMap.class, TreeMap::new);
        mapSuppliers.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
        mapSuppliers.put(ConcurrentMap.class, ConcurrentHashMap::new);
        mapSuppliers.put(HashMap.class, HashMap::new);
        mapSuppliers.put(Map.class, HashMap::new);
        mapSuppliers.put(LinkedHashMap.class, LinkedHashMap::new);

        Supplier<Object> supplier = mapSuppliers.get(clazz);
        if (supplier != null) {
            return (T) supplier.get();
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            if (e instanceof InstantiationException) {
                throw (InstantiationException) e;
            } else if (e instanceof IllegalAccessException) {
                throw (IllegalAccessException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
