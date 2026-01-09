package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.entity.base.AbstractUpdate;
import cn.wubo.sql.forge.entity.base.EntityCondition;
import cn.wubo.sql.forge.entity.base.EntitySet;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityUpdate<T> extends AbstractUpdate<T, Integer, EntityUpdate<T>> {

    public EntityUpdate(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Integer run(CacheService cacheService, CrudService crudService) throws Exception {
        TableStructureInfo tableStructureInfo = cacheService.getTableInfo(entityClass);

        Map<String, Object> sqlSets = new HashMap<>();
        if (sets != null && !sets.isEmpty()){
            for (EntitySet<T> entitySet : sets) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entitySet.column());
                if (columnInfo != null) {
                    sqlSets.put(columnInfo.getColumnName(), entitySet.value());
                }
            }
        }

        List<Where> sqlWheres = new ArrayList<>();
        if (entityConditions != null && !entityConditions.isEmpty()) {
            for (EntityCondition<T> entityCondition : entityConditions) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entityCondition.column());
                if (columnInfo != null) {
                    sqlWheres.add(new Where(columnInfo.getColumnName(), entityCondition.condition(), entityCondition.value()));
                }
            }
        }

        Update update = new Update(
                sqlSets,
                sqlWheres,
                null
        );

        return (Integer) crudService.update(tableStructureInfo.getTableName(), update);
    }
}
