package cn.wubo.sql.forge;

import cn.wubo.sql.forge.enums.ConditionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MasterTable extends BaseTable{

    private List<Search> search;

    private List<SlaveTable> slaveTables;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Search extends Column{
        private ConditionType conditionType;
    }
}
