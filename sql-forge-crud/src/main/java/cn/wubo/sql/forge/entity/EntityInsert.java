package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.base.Set;
import cn.wubo.sql.forge.entity.base.AbstractInsert;
import cn.wubo.sql.forge.entity.base.EntitySet;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityInsert<T> extends AbstractInsert<T, Object, EntityInsert<T>> {

    public EntityInsert(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Object run(CacheService cacheService, CrudService crudService) throws Exception {
        TableStructureInfo tableStructureInfo = cacheService.getTableInfo(entityClass);

        List<Set> sqlSets = new ArrayList<>();
        if (sets != null && !sets.isEmpty()){
            for (EntitySet<T> entitySet : sets) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entitySet.column());
                if (columnInfo != null) {
                    sqlSets.add(new Set(columnInfo.getColumnName(), entitySet.value()));
                }
            }
        }

        Insert insert = new Insert(sqlSets, null);
        return crudService.insert(tableStructureInfo.getTableName(), insert);
    }
}
