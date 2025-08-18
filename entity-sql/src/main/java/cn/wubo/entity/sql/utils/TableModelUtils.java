package cn.wubo.entity.sql.utils;

import cn.wubo.entity.sql.cache.CacheHelper;
import cn.wubo.entity.sql.core.annotations.Column;
import cn.wubo.entity.sql.core.annotations.Key;
import cn.wubo.entity.sql.core.annotations.Table;
import cn.wubo.entity.sql.core.model.ColumnModel;
import cn.wubo.entity.sql.core.model.ItemModel;
import cn.wubo.entity.sql.core.model.TableModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TableModelUtils {

    private TableModelUtils() {
    }

    /**
     * 根据给定的类获取对应的表模型信息
     *
     * @param clazz 需要解析的类对象
     * @return 包含表名、初始化标志和列信息的表模型对象
     */
    public static TableModel getTableModel(Class<?> clazz) {
        TableModel tableModel = CacheHelper.tableModelCache.get(clazz);

        if (tableModel != null) return tableModel;

        tableModel = new TableModel();

        // 获取类上的@Table注解信息
        Table table = clazz.getAnnotation(Table.class);

        if (table != null) {
            // 如果存在@Table注解，设置表名和初始化标志
            tableModel.setTableName(StringUtils.defaultValue(table.value(), StringUtils.toSnakeCase(clazz.getSimpleName())));
            tableModel.setInit(table.init());
        } else {
            // 如果不存在@Table注解，使用类名的蛇形命名作为表名
            tableModel.setTableName(StringUtils.toSnakeCase(clazz.getSimpleName()));
        }

        // 设置表的列模型信息
        tableModel.setColumns(getColumnModels(clazz));

        CacheHelper.tableModelCache.put(clazz, tableModel);

        return tableModel;
    }

    /**
     * 获取指定类的所有列模型信息
     *
     * @param clazz 需要获取列模型信息的类
     * @return 包含所有列模型的列表
     */
    public static List<ColumnModel> getColumnModels(Class<?> clazz) {
        List<ColumnModel> columns = new ArrayList<>();
        // 收集类的所有列信息
        allColumns(clazz, columns);
        return columns;
    }

    /**
     * 递归收集指定类及其父类中所有符合要求的字段，并封装为 ColumnModel 添加到列表中。
     * <p>
     * 要求字段必须是非静态、非final、非合成字段。
     * 对于每个字段，会解析其上的 @Key 或 @Column 注解信息，填充 ColumnModel 的相关属性。
     * 如果没有注解，则使用字段名的下划线命名作为默认列名。
     *
     * @param clazz   当前处理的类，如果为 null 则直接返回
     * @param columns 用于收集 ColumnModel 的列表
     */
    private static void allColumns(Class<?> clazz, List<ColumnModel> columns) {
        if (clazz == null) {
            return;
        }

        // 获取当前类中所有非静态非final字段
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isSynthetic()).filter(field -> {
            int modifiers = field.getModifiers();
            return !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers);
        }).forEach(field -> {
            ColumnModel columnModel = new ColumnModel();
            columnModel.setF(field);
            field.setAccessible(true); // 提前设置可访问性

            String fieldName = field.getName();
            String defaultColumn = StringUtils.toSnakeCase(fieldName);

            Annotation[] columnAnns = field.getAnnotations();

            Annotation keyAnn = null;
            Annotation columnAnn = null;

            // 一次遍历找出 Key 和 Column 注解
            for (Annotation ann : columnAnns) {
                if (ann instanceof Key) {
                    keyAnn = ann;
                } else if (ann instanceof Column) {
                    columnAnn = ann;
                }
            }

            // 处理 @Key 注解：标识主键字段，设置不可查看、编辑、搜索
            if (keyAnn != null) {
                Key key = (Key) keyAnn;
                columnModel.setField(fieldName);
                columnModel.setColumn(defaultColumn);
                columnModel.setLabel(defaultColumn);
                columnModel.getView().setViewable(false);
                columnModel.getEdit().setEditable(false);
                columnModel.getSearch().setSearchable(false);
                columnModel.setIsKey(true);
                columnModel.setGenerationType(key.value());
            }
            // 处理 @Column 注解：详细配置字段展示、编辑、搜索等行为
            else if (columnAnn != null) {
                Column column = (Column) columnAnn;
                columnModel.setField(fieldName);
                columnModel.setColumn(column.value());
                columnModel.setLabel(StringUtils.defaultValue(column.label(), column.value()));
                columnModel.setItems(Arrays.stream(column.items()).map(item -> new ItemModel(item.value(), item.label())).toList());
                columnModel.setType(column.type());

                // @formatter:off
                columnModel.getView()
                        .setViewable(column.view().viewable())
                        .setSortable(column.view().sortable())
                        .setExportable(column.view().exportable()).
                        setWidth(column.view().width())
                        .setViewOrder(column.view().viewOrder());

                columnModel.getEdit()
                        .setEditable(column.edit().editable())
                        .setType(column.edit().type())
                        .setRequired(column.edit().required())
                        .setEditOrder(column.edit().editOrder())
                        .setPlaceholder(column.edit().placeholder());

                columnModel.getSearch()
                        .setSearchable(column.search().searchable())
                        .setType(column.search().type())
                        .setCondition(column.search().condition())
                        .setSearchOrder(column.search().searchOrder());
                // @formatter:on
            }
            // 没有注解时使用默认配置
            else {
                columnModel.setField(fieldName);
                columnModel.setColumn(defaultColumn);
                columnModel.setLabel(defaultColumn);
            }

            columns.add(columnModel);
        });

        // 递归处理父类
        allColumns(clazz.getSuperclass(), columns);
    }

    /**
     * 将Map转换为指定类型的对象实例
     *
     * @param map         包含属性名值对的Map，键为属性名，值为属性值
     * @param entityClass 目标对象的Class类型
     * @param <T>         泛型参数，表示目标对象的类型
     * @return 转换后的对象实例
     * @throws NoSuchMethodException     当目标类没有无参构造函数时抛出
     * @throws InvocationTargetException 当构造函数调用异常时抛出
     * @throws InstantiationException    当实例化对象失败时抛出
     * @throws IllegalAccessException    当访问构造函数或字段时没有权限时抛出
     * @throws ParseException            当属性值转换过程中发生解析错误时抛出
     */
    public static <T> T convertToObject(Map<String, Object> map, Class<T> entityClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException {
        // 创建目标类的实例对象
        T obj = (T) entityClass.getDeclaredConstructor().newInstance();
        TableModel tableModel = TableModelUtils.getTableModel(entityClass);

        // 遍历Map中的每个属性，将其值设置到对象的对应字段中
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String propertyName = StringUtils.toSnakeCase(entry.getKey());
            Object propertyValue = entry.getValue();

            ColumnModel col = tableModel.getColumnModelByColumn(propertyName);
            col.getF().setAccessible(true);
            col.getF().set(obj, DatabaseUtils.transValueDb2Obj(col, propertyValue));
        }

        return obj;
    }


}
