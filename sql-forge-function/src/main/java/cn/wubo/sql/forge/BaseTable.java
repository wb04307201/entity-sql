package cn.wubo.sql.forge;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaseTable {

    private String tableName;
    private String title;
    private String rowKey;
    private String parentColumn;
    private String childColumn;
    private List<Column> columns;

    @Data
    public static class Column {
        private String title;
        private String columnName;
        Map<String, Object> items;
        private boolean isHidden = false;
    }
}
