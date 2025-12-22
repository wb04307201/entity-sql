package cn.wubo.sql.forge;

import cn.wubo.sql.forge.enums.InputType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
public class BaseTable {

    private String tableName;
    private String tableLabel;
    private String rowKey;
    private String parentColumn;
    private String childColumn;
    private List<Column> columns;

    @Data
    public static class Column {
        private String columnLabel;
        private String columnName;
        Map<String, Object> items;
        private boolean isHidden = false;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Edit extends Column {
        private InputType inputType;
        private boolean isRequired = false;
        private String placeholder = "";
        private int precision;
        private int scale;
        private int length;
        private boolean editable = true;
        private boolean creatable = true;
    }
}
