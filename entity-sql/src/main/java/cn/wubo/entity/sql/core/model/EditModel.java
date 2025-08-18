package cn.wubo.entity.sql.core.model;

import cn.wubo.entity.sql.core.enums.EditType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EditModel {
    private Boolean editable = true;
    private EditType type = EditType.TEXT;
    private Boolean required = false;
    private Integer editOrder = 100;
    private String placeholder = "";
}
