package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.SqlScript;

import java.sql.SQLException;
import java.util.Map;

public record ApiTemplateExcutor(
        IApiTemplateStorage apiTemplateStorage,
        Executor executor
) {

    public Object execute(String id, Map<String, Object> params) throws SQLException {
        ApiTemplate apiTemplate = apiTemplateStorage.get(id);
        if (Boolean.FALSE.equals(apiTemplate.getIsApproved()))
            throw new IllegalArgumentException("api template not approved");
        SqlTemplateEngine engine = new SqlTemplateEngine();
        SqlScript result = engine.process(apiTemplate.getContext(), params);
        return executor.execute(result);
    }

}
