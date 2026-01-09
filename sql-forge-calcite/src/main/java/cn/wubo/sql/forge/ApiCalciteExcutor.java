package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import cn.wubo.sql.forge.utils.CalciteExcutorUtils;
import cn.wubo.sql.forge.utils.ResultSetUtils;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public record ApiCalciteExcutor(
        IApiCalciteStorage calciteStorage
) {

    public List<RowMap> execute(@NotNull String id, Map<String, Object> params) throws SQLException {
        ApiTemplate apiTemplate = calciteStorage.get(id);
        SqlTemplateEngine engine = new SqlTemplateEngine();
        SqlScript result = engine.process(apiTemplate.getContext(), params, SqlGenerationMode.WITH_VALUES);

        return CalciteExcutorUtils.execute(calciteStorage.getConfig().getContext(),result);
    }
}
