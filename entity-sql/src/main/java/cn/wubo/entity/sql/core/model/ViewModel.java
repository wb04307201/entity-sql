package cn.wubo.entity.sql.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ViewModel {
    private Boolean viewable =  true;
    private Boolean sortable =  true;
    private Boolean exportable =  true;
    private Integer width =  200;
    private Integer viewOrder =  100;
}
