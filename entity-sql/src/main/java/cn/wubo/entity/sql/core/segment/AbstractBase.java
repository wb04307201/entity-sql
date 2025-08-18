package cn.wubo.entity.sql.core.segment;

import cn.wubo.entity.sql.core.enums.StatementType;
import cn.wubo.entity.sql.core.model.ColumnModel;
import cn.wubo.entity.sql.core.model.TableModel;
import cn.wubo.entity.sql.utils.DatabaseUtils;
import cn.wubo.entity.sql.utils.FunctionUtils;
import cn.wubo.entity.sql.utils.StringUtils;
import cn.wubo.entity.sql.utils.TableModelUtils;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wubo.entity.sql.core.enums.StatementCondition.*;
import static org.springframework.boot.jdbc.DatabaseDriver.*;

public abstract class AbstractBase<T, Children extends IBase<T, Children, R>, R> implements IBase<T, Children, R> {

    protected final Children typedThis = (Children) this;

    protected Class<T> entityClass;

    protected TableModel tableModel;

    protected DatabaseDriver databaseDriver;

    protected List<String> columns = new ArrayList<>();
    protected List<Set> sets = new ArrayList<>();
    protected List<Condition> conditions = new ArrayList<>();

    protected StringBuilder sb = new StringBuilder();
    protected Map<Integer, Object> params = new HashMap<>();

    protected StatementType statementType;

    protected int page;
    protected int pageSize;

    public AbstractBase(Class<T> entityClass, StatementType statementType) {
        this.entityClass = entityClass;
        this.statementType = statementType;
        this.tableModel = TableModelUtils.getTableModel(entityClass);
    }

    @Override
    public R execute(Connection connection) {
        sb = new StringBuilder();
        params.clear();

        try {
            this.databaseDriver = DatabaseUtils.getDatabaseDriver(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        switch (statementType) {
            case INSERT:
                insertSql();
                break;
            case UPDATE:
                updateSql();
                break;
            case SELECT:
                selectSql();
                break;
            case DELETE:
                deleteSql();
                break;
            case CREATE:
                createSql();
                break;
            case DROP:
                dropSql();
                break;
        }
        return executeSql(connection);
    }

    protected abstract R executeSql(Connection connection);

    protected void insertSql() {
        // @formatter:off
        sb.append(StatementType.INSERT.getValue())
                .append(
                        tableModel.getTableName()).append(" (").append(sets.stream().map(set -> {

                            ColumnModel columnModel = tableModel.getColumnModelByColumn(set.getColumn());
                                    params.put(params.size() + 1, set.getValue());
                                    return set.getColumn();
                                })
                                .collect(Collectors.joining(","))
                )
                .append(") VALUES (")
                .append(
                        sets.stream().map(set -> "?").collect(Collectors.joining(","))
                )
                .append(")");
        // @formatter:on
    }

    protected void whereSql() {
        // 将where条件列表转换为SQL语句字符串
        String whereSQL = conditions.stream().map(condition -> {
            ColumnModel columnModel = tableModel.getColumnModelByColumn(condition.getColumn());
            // 检查条件是否为OR条件
            if (FunctionUtils.compileConditionOr(condition, t -> condition.getStatementCondition() == EQ, t -> condition.getStatementCondition() == NOT_EQ, t -> condition.getStatementCondition() == GT, t -> condition.getStatementCondition() == GTEQ, t -> condition.getStatementCondition() == LT, t -> condition.getStatementCondition() == LTEQ)) {
                params.put(params.size() + 1, condition.getValue());
                return condition.getColumn() + condition.getStatementCondition().getValue() + "?";
            } else if (FunctionUtils.compileConditionOr(condition, t -> condition.getStatementCondition() == LIKE, t -> condition.getStatementCondition() == NOT_LIKE)) {
                String valueStr = "%" + condition.getValue() + "%";
                params.put(params.size() + 1, valueStr);
                return condition.getColumn() + condition.getStatementCondition().getValue() + "?";
            } else if (condition.getStatementCondition() == LEFT_LIKE) {
                String valueStr = "%" + condition.getValue();
                params.put(params.size() + 1, valueStr);
                return condition.getColumn() + condition.getStatementCondition().getValue() + "?";
            } else if (condition.getStatementCondition() == RIGHT_LIKE) {
                String valueStr = condition.getValue() + "%";
                params.put(params.size() + 1, valueStr);
                return condition.getColumn() + condition.getStatementCondition().getValue() + "?";
            } else if (FunctionUtils.compileConditionOr(condition, t -> condition.getStatementCondition() == BETWEEN, t -> condition.getStatementCondition() == NOT_BETWEEN)) {
                // 处理BETWEEN和NOT BETWEEN条件
                return FunctionUtils.buildCondition(condition, t -> FunctionUtils.compileConditionAnd(t, tt -> tt.getValue() instanceof List, tt -> ((List<?>) tt.getValue()).size() == 2), t -> {
                    List<?> valueObjs = (List<?>) condition.getValue();
                    params.put(params.size() + 1, valueObjs.get(0));
                    params.put(params.size() + 1, valueObjs.get(1));
                    return condition.getColumn() + condition.getStatementCondition().getValue() + "? AND ?";
                });
            } else if (FunctionUtils.compileConditionOr(condition, t -> condition.getStatementCondition() == IN, t -> condition.getStatementCondition() == NOT_IN)) {
                // 处理IN和NOT IN条件
                return FunctionUtils.buildCondition(condition, t -> condition.getValue() instanceof List, t -> {
                    List<?> valueObjs = (List<?>) condition.getValue();
                    if (valueObjs.isEmpty()) {
                        return "1 = 2";
                    } else {
                        valueObjs.forEach(valueObj -> params.put(params.size() + 1, valueObj));
                        return condition.getColumn() + condition.getStatementCondition().getValue() + "(" + valueObjs.stream().map(obj -> "?").collect(Collectors.joining(",")) + ")";
                    }
                });
            } else if (FunctionUtils.compileConditionOr(condition, t -> condition.getStatementCondition() == IS_NUll, t -> condition.getStatementCondition() == IS_NOT_NUll)) {
                // 处理NULL和NOT NULL条件
                return condition.getColumn() + condition.getStatementCondition().getValue();
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.joining(" AND "));
        // 如果生成的SQL不为空，则追加到WHERE子句前
        if (!whereSQL.isEmpty()) sb.append(" WHERE ").append(whereSQL);
    }

    protected void updateSql() {
        sb.append(StatementType.UPDATE.getValue()).append(tableModel.getTableName()).append(" SET ");

        sb.append(sets.stream().map(set -> {
            ColumnModel columnModel = tableModel.getColumnModelByColumn(set.getColumn());
            params.put(params.size() + 1, set.getValue());
            return set.getColumn() + " = ?";
        }).collect(Collectors.joining(",")));

        whereSql();
    }

    protected void selectSql() {
        sb.append(StatementType.SELECT.getValue());

        if (!columns.isEmpty()) {
            // 遍历字段列表，依次添加到SQL中
            columns.forEach(column -> sb.append(column).append(","));
        } else {
            // 若未指定字段，默认查询所有字段
            sb.append("*,");
        }

        sb.delete(sb.length() - 1, sb.length()).append(" FROM ").append(tableModel.getTableName());

        whereSql();
        pageSql();
    }

    protected void deleteSql() {
        sb.append(StatementType.DELETE.getValue()).append(tableModel.getTableName());

        whereSql();
    }

    protected void createSql() {
        sb.append(StatementType.CREATE.getValue()).append(tableModel.getTableName()).append(" ( ");

        tableModel.getColumns().forEach(col -> sb.append(col.definition()).append(","));
        sb.delete(sb.length() - 1, sb.length()).append(")");
    }

    protected void dropSql() {
        sb.append(StatementType.DROP.getValue()).append(tableModel.getTableName());
    }

    protected <T> T convertToObject(Map<String, Object> map) throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException {
        T obj = (T) entityClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String propertyName = StringUtils.toSnakeCase(entry.getKey());
            Object propertyValue = entry.getValue();

            ColumnModel col = tableModel.getColumnModelByColumn(propertyName);
            col.getF().setAccessible(true);
            col.getF().set(obj, DatabaseUtils.transValueDb2Obj(col, propertyValue));
        }

        return obj;
    }

    protected void pageSql() {
        if (pageSize > 0) {
            if (databaseDriver == ORACLE || databaseDriver == SQLSERVER)
                sb.append(" LIMIT ").append(pageSize).append(" ROWS FETCH NEXT ").append(page * pageSize).append(" ROWS ONLY");

            else sb.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(page * pageSize);
        }
    }
}
