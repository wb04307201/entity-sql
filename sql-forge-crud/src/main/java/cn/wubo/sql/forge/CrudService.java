package cn.wubo.sql.forge;

import cn.wubo.sql.forge.jdbc.SQL;
import cn.wubo.sql.forge.map.ParamMap;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.crud.base.Join;
import cn.wubo.sql.forge.crud.base.Set;
import cn.wubo.sql.forge.crud.base.Where;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.sql.SQLException;
import java.util.List;

import static cn.wubo.sql.forge.constant.Constant.QUESTION_MARK;

public record CrudService(Executor executor) {

    public Object delete(@NotBlank String tableName, @Valid Delete delete) throws SQLException {
        ParamMap params = new ParamMap();
        SQL sql = new SQL().DELETE_FROM(tableName);

        applyWheres(sql, delete.wheres(), params);

        int count = executor.executeUpdate(new SqlScript(sql.toString(), params));

        if (delete.select() != null)
            return select(tableName, delete.select());
        else
            return count;
    }

    public Object insert(@NotBlank String tableName, @Valid Insert insert) throws SQLException {
        ParamMap params = new ParamMap();
        SQL sql = new SQL().INSERT_INTO(tableName);

        for (Set set : insert.sets()) {
            params.put(set.value());
            sql.VALUES(set.column(), QUESTION_MARK);
        }

        Object key = executor.executeInsert(new SqlScript(sql.toString(), params));

        if (insert.select() != null)
            return select(tableName, insert.select());
        else
            return key;
    }

    public List<RowMap> select(@NotBlank String tableName, @Valid Select select) throws SQLException {
        ParamMap params = new ParamMap();
        SQL sql = new SQL().FROM(tableName);
        String[] columns = select.columns() == null || select.columns().length == 0 ? new String[]{"*"} : select.columns();
        if (select.distinct())
            sql.SELECT_DISTINCT(columns);
        else
            sql.SELECT(columns);

        if (select.joins() != null && !select.joins().isEmpty()) {
            for (Join join : select.joins()) {
                switch (join.type()) {
                    case INNER_JOIN -> sql.INNER_JOIN(join.on());
                    case LEFT_OUTER_JOIN -> sql.LEFT_OUTER_JOIN(join.on());
                    case RIGHT_OUTER_JOIN -> sql.RIGHT_OUTER_JOIN(join.on());
                    case OUTER_JOIN -> sql.OUTER_JOIN(join.on());
                    default -> sql.JOIN(join.on());
                }
            }
        }

        applyWheres(sql, select.wheres(), params);

        if (select.groups() != null && select.groups().length > 0)
            sql.GROUP_BY(select.groups());


        if (select.orders() != null && select.orders().length > 0)
            sql.ORDER_BY(select.orders());

        if (select.page() != null) {
            long offset = (long) select.page().pageIndex() * select.page().pageSize();
            params.put(offset);
            params.put(select.page().pageSize());
            sql.OFFSET(QUESTION_MARK)
                    .LIMIT(QUESTION_MARK);
        }

        return executor.executeQuery(new SqlScript(sql.toString(), params));
    }


    public Object update(@NotBlank String tableName, @Valid Update update) throws SQLException {
        ParamMap params = new ParamMap();
        SQL sql = new SQL().UPDATE(tableName);

        applyWheres(sql, update.wheres(), params);

        int count = executor.executeUpdate(new SqlScript(sql.toString(), params));

        if (update.select() != null)
            return select(tableName, update.select());
        else
            return count;
    }

    private void applyWheres(SQL sql, List<Where> wheres, ParamMap params) {
        if (wheres != null && !wheres.isEmpty()) {
            for (Where where : wheres) {
                sql.WHERE(where.create(params));
            }
        }
    }
}
