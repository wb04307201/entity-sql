package cn.wubo.sql.forge;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SlaveTable extends BaseTable{

    private String masterColumnName;
    private String slaveColumnName;

    private List<SlaveTable> slaveTables;
}
