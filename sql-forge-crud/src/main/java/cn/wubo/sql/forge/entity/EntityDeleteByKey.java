package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.entity.base.AbstractEntity;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;
import cn.wubo.sql.forge.enums.ConditionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityDeleteByKey<T> extends AbstractEntity<T, Integer, EntityDeleteByKey<T>> {

    public EntityDeleteByKey(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Integer run(CacheService cacheService, CrudService crudService) throws Exception {
        TableStructureInfo tableStructureInfo = cacheService.getTableInfo(entityClass);

        Optional<ColumnInfo> primaryKeyColumnInfoOptional = tableStructureInfo.getColumnInfos().stream().filter(ColumnInfo::isPrimaryKey).findAny();

        if (primaryKeyColumnInfoOptional.isEmpty())
            throw new IllegalArgumentException("Entity class has no primary key");

        ColumnInfo primaryKeyColumnInfo = primaryKeyColumnInfoOptional.get();
        Field primaryKeyField = primaryKeyColumnInfo.getField();
        primaryKeyField.setAccessible(true);
        Object primaryKeyValue = primaryKeyField.get(entity);
        if (primaryKeyValue == null){
            throw new IllegalArgumentException("Entity class primary key value is null");
        }

        List<Where> sqlWheres = new ArrayList<>();
        sqlWheres.add(new Where(primaryKeyColumnInfo.getColumnName(), ConditionType.EQ, primaryKeyValue));
        Delete delete = new Delete(sqlWheres,null);

        return (Integer) crudService.delete(tableStructureInfo.getTableName(), delete);
    }
}
