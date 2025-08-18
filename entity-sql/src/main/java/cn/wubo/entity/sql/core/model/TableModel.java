package cn.wubo.entity.sql.core.model;

import cn.wubo.entity.sql.core.functional_interface.SFunction;
import cn.wubo.entity.sql.utils.LambdaUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class TableModel {

    private String tableName;
    private List<ColumnModel> columns = new ArrayList<>();
    private Boolean init = false;

    public <T> String getColumnByField(SFunction<T, ?> fn) {
        String field = LambdaUtils.getFieldName(fn);
        // @formatter:off
        return columns.stream()
                .filter(columnModel -> columnModel.getField().equals(field))
                .findAny()
                .orElseThrow(() -> new  IllegalArgumentException("Column model not found for field: " + field))
                .getColumn();
        // @formatter:on
    }

    public ColumnModel getColumnModelByColumn(String column) {
        // @formatter:off
        return columns.stream()
                .filter(columnModel -> columnModel.getColumn().equalsIgnoreCase(column))
                .findAny()
                .orElseThrow(() -> new  IllegalArgumentException("Column model not found for column: " + column));
        // @formatter:on
    }
}
