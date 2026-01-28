package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.inter.IExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogSelectExecute implements IExecute<Select> {
    @Override
    public Select before(String tableName, Select select) {
        log.info("tableName: {} SQL: {}", tableName, select);
        return select;
    }
}
