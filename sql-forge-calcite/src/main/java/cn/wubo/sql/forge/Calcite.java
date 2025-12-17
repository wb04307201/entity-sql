package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import cn.wubo.sql.forge.utils.ResultSetUtils;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public record Calcite(
        ICalciteStorage calciteStorage
) {

    public List<RowMap> execute(String id, Map<String, Object> params) throws SQLException {
        String config = calciteStorage.getComfig();
        if (config == null || config.trim().isEmpty()) {
            throw new SQLException("config is null");
        }

        Properties info = new Properties();
        info.setProperty("model", "inline:" + config);
        info.setProperty("lex", "JAVA");

        ApiTemplate apiTemplate = calciteStorage.get(id);
        SqlTemplateEngine engine = new SqlTemplateEngine();
        SqlScript result = engine.process(apiTemplate.getContext(), params, SqlGenerationMode.WITH_VALUES);

        try (Connection conn = DriverManager.getConnection("jdbc:calcite:", info)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            try (ResultSet resultSet = stmt.executeQuery(result.sql())) {
                return ResultSetUtils.resultSetToList(resultSet);
            }
        }
    }
}
