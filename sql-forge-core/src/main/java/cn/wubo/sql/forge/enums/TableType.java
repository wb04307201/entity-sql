package cn.wubo.sql.forge.enums;

import cn.wubo.sql.forge.records.TableTypeR;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TableType {

    TABLE("TABLE", "普通表（基本表）"),
    VIEW("VIEW", "视图"),
    SYSTEM_TABLE("SYSTEM TABLE", "系统表"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY", "全局临时表"),
    LOCAL_TEMPORARY("LOCAL TEMPORARY", "本地临时表"),
    ALIAS("ALIAS", "别名表"),
    SYNONYM("SYNONYM", "同义词");

    private final String type;
    private final String description;

    TableType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public TableTypeR getTableTypeR() {
        return new TableTypeR(type, description);
    }

    public static List<TableTypeR> getAllTypes() {
        return  Arrays.stream(TableType.values())
                .map(TableType::getTableTypeR)
                .toList();
    }
}
