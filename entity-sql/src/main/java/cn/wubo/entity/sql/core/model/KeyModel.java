package cn.wubo.entity.sql.core.model;

import cn.wubo.entity.sql.core.enums.GenerationType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class KeyModel {
    private Boolean isKey = false;
    private GenerationType type = GenerationType.UUID;
}
