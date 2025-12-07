package cn.wubo.sql.forge.entity;

import cn.wubo.sql.forge.CrudService;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.entity.base.AbstractSelect;
import cn.wubo.sql.forge.entity.base.EntityCondition;
import cn.wubo.sql.forge.entity.base.EntityOrder;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.entity.cache.ColumnInfo;
import cn.wubo.sql.forge.entity.cache.TableStructureInfo;
import cn.wubo.sql.forge.entity.inter.SFunction;
import cn.wubo.sql.forge.jdbc.SQL;
import cn.wubo.sql.forge.map.RowMap;

import java.util.ArrayList;
import java.util.List;

public class EntitySelect<T> extends AbstractSelect<T, List<T>, EntitySelect<T>> {

    public EntitySelect(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public List<T> build(CacheService cacheService, CrudService crudService) {
        TableStructureInfo tableStructureInfo = cacheService.getTableInfo(entityClass);

        SQL sql = new SQL();
        sql.FROM(tableStructureInfo.getTableName());

        List<String> sqlColumns = new ArrayList<>();
        if (columns != null && !columns.isEmpty())
            for (SFunction<T, ?> column : columns) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(column);
                if (columnInfo != null)
                    sqlColumns.add(columnInfo.getColumnName());
            }
        if (!sqlColumns.isEmpty())
            sql.SELECT(sqlColumns.toArray(new String[0]));
        else
            sql.SELECT("*");

        List<Where> sqlWheres = new ArrayList<>();
        if (entityConditions != null && !entityConditions.isEmpty()) {
            for (EntityCondition<T> entityCondition : entityConditions) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entityCondition.column());
                if (columnInfo != null) {
                    sqlWheres.add(new Where(columnInfo.getColumnName(), entityCondition.condition(), entityCondition.value()));
                }
            }
        }

        List<String> sqlOrders = new ArrayList<>();
        if (orders != null && !orders.isEmpty()) {
            for (EntityOrder<T> entityOrder : orders) {
                ColumnInfo columnInfo = tableStructureInfo.getColumnInfo(entityOrder.colum());
                if (columnInfo != null) {
                    sqlOrders.add(columnInfo.getColumnName() + entityOrder.orderType().getValue());
                }
            }
        }

        Select select = new Select(
                sqlColumns.toArray(new String[0]),
                sqlWheres,
                page,
                null,
                sqlOrders.toArray(new String[0]),
                null,
                false
        );
        List<RowMap> list = crudService.select(tableStructureInfo.getTableName(), select);


        return null;
    }


}
