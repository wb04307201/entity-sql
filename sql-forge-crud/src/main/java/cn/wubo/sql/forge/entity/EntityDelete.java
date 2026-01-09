package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.entity.base.AbstractDelete;
import cn.wubo.sql.forge.entity.base.EntityCondition;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityDelete<T> extends AbstractDelete<T, Integer, EntityDelete<T>> {

    public EntityDelete(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Integer run(CacheService cacheService, CrudService crudService) throws Exception {
        TableStructureInfo tableStructureInfo = cacheService.getTableInfo(entityClass);

        List<Where> sqlWheres = new ArrayList<>();
        if (entityConditions != null && !entityConditions.isEmpty()) {
            for (EntityCondition<T> entityCondition : entityConditions) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entityCondition.column());
                if (columnInfo != null) {
                    sqlWheres.add(new Where(columnInfo.getColumnName(), entityCondition.condition(), entityCondition.value()));
                }
            }
        }

        Delete delete = new Delete(
                sqlWheres,
                null
        );

        return (Integer) crudService.delete(tableStructureInfo.getTableName(), delete);
    }
}
