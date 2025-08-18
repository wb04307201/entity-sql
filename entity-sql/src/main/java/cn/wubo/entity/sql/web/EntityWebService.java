package cn.wubo.entity.sql.web;

import cn.wubo.entity.sql.DataSourceHelper;
import cn.wubo.entity.sql.core.Entity;
import cn.wubo.entity.sql.core.SQL;
import cn.wubo.entity.sql.core.model.TableModel;
import cn.wubo.entity.sql.exception.EntityWebException;
import cn.wubo.entity.sql.utils.FreemarkerUtils;
import cn.wubo.entity.sql.utils.TableModelUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityWebService {

    private static Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    DataSourceHelper dataSourceHelper;

    public EntityWebService(DataSourceHelper dataSourceHelper) {
        this.dataSourceHelper = dataSourceHelper;
    }

    /**
     * 构建并注册指定ID和类的映射关系，如果对应的表不存在则创建表
     *
     * @param id    类映射的标识符
     * @param clazz 需要注册的类对象
     */
    public void build(String id, Class<?> clazz) {
        // 注册类映射关系
        classMap.put(id, clazz);

        // 获取类对应的表模型信息
        TableModel tableModel = TableModelUtils.getTableModel(clazz);

        // 如果模型需要初始化且表不存在，则创建表
        if (tableModel.getInit() && !dataSourceHelper.execute(SQL.isTableExists(clazz)))
            dataSourceHelper.execute(SQL.createTable(clazz));
    }

    /**
     * 根据实体ID生成表格视图
     *
     * @param id          实体ID，用于查找对应的实体类
     * @param contextPath 上下文路径，用于生成链接地址
     * @return 返回渲染后的表格HTML字符串
     * @throws TemplateException 模板处理异常
     * @throws IOException       IO读写异常
     */
    public String view(String id, String contextPath) throws TemplateException, IOException {
        // 检查是否存在指定ID的实体类
        if (classMap.containsKey(id)) {
            // 构造模板数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("contextPath", contextPath);
            map.put("id", id);
            map.put("data", TableModelUtils.getTableModel(classMap.get(id)));
            // 使用Freemarker模板引擎渲染表格
            return FreemarkerUtils.write("table.ftl", map);
        } else {
            // 实体不存在时抛出异常
            throw new EntityWebException("Entity with id '" + id + "' not found");
        }
    }

    /**
     * 根据实体ID和查询参数执行数据查询操作
     *
     * @param id     实体ID，用于在classMap中查找对应的实体类
     * @param params 查询参数Map，包含查询条件等参数，其中"wheres"键对应查询条件
     * @return 查询结果列表，包含符合查询条件的实体对象
     * @throws EntityWebException 当指定ID的实体不存在时抛出异常
     */
    public List<Object> select(String id, Map<String, Object> params) {
        // 检查是否存在指定ID的实体类
        if (classMap.containsKey(id)) {
            Class<?> clazz = classMap.get(id);
            // 执行查询操作，将参数转换为实体查询对象并执行数据源操作
            return dataSourceHelper.execute(Entity.query(JSON.parseObject(JSONObject.toJSONString(params.getOrDefault("wheres", new HashMap<String, Object>())), clazz)));
        } else {
            // 实体ID不存在时抛出异常
            throw new EntityWebException("Entity with id '" + id + "' not found");
        }
    }

    /**
     * 保存实体数据，根据ID查找对应的实体类，将参数转换为实体对象并执行插入或更新操作
     *
     * @param id     实体ID，用于在classMap中查找对应的实体类
     * @param params 包含实体数据的参数映射，其中"wheres"键对应的值将被转换为实体对象
     * @return 保存操作的结果对象
     * @throws EntityWebException 当指定ID的实体类不存在时抛出异常
     */
    public Object save(String id, Map<String, Object> params) {
        // 检查是否存在指定ID的实体类
        if (classMap.containsKey(id)) {
            Class<?> clazz = classMap.get(id);
            // 将参数转换为实体对象并执行保存操作
            return dataSourceHelper.execute(Entity.insertOrUpdate(JSON.parseObject(JSONObject.toJSONString(params), clazz)));
        } else {
            // 实体类不存在时抛出异常
            throw new EntityWebException("Entity with id '" + id + "' not found");
        }
    }

    /**
     * 根据ID获取实体对象
     *
     * @param id     实体ID，用于在classMap中查找对应的实体类
     * @param params 包含查询参数的Map，其中"wheres"键对应的值将被用作查询条件
     * @return 返回根据ID和查询条件获取到的实体对象
     * @throws EntityWebException 当指定ID的实体不存在时抛出异常
     */
    public Object getById(String id, Map<String, Object> params) {
        // 检查是否存在指定ID的实体类
        if (classMap.containsKey(id)) {
            Class<?> clazz = classMap.get(id);
            // 执行数据查询操作，将params中的wheres条件转换为实体对象进行查询
            return dataSourceHelper.execute(Entity.grtById(JSON.parseObject(JSONObject.toJSONString(params), clazz)));
        } else {
            // 如果实体ID不存在，抛出异常
            throw new EntityWebException("Entity with id '" + id + "' not found");
        }
    }

    /**
     * 根据ID列表删除实体记录
     *
     * @param id     实体ID，用于在classMap中查找对应的实体类
     * @param params 包含删除数据的参数Map，其中"data"键对应要删除的记录列表
     * @return 返回成功删除的记录数量
     * @throws EntityWebException 当指定ID的实体不存在时抛出异常
     */
    public Integer deleteByIds(String id, Map<String, Object> params) {
        // 检查是否存在指定ID的实体类
        if (classMap.containsKey(id)) {
            Class<?> clazz = classMap.get(id);
            // 从参数中获取要删除的数据列表，如果不存在则使用空列表
            List<Map<String, Object>> rows = (List<Map<String, Object>>) params.getOrDefault("data", new ArrayList<Map<String, Object>>());
            Integer count = 0;
            // 遍历数据列表，逐条执行删除操作
            for (Map<String, Object> row : rows) {
                count += dataSourceHelper.execute(Entity.deleteById(JSON.parseObject(JSONObject.toJSONString(row), clazz)));
            }

            return count;
        } else {
            throw new EntityWebException("Entity with id '" + id + "' not found");
        }
    }

}
