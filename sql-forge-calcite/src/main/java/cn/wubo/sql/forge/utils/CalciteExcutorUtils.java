package cn.wubo.sql.forge.utils;

import cn.wubo.sql.forge.ApiTemplate;
import cn.wubo.sql.forge.SqlGenerationMode;
import cn.wubo.sql.forge.SqlTemplateEngine;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import lombok.experimental.UtilityClass;

import java.sql.*;
import java.util.List;
import java.util.Properties;

@UtilityClass
public class CalciteExcutorUtils {

    public List<RowMap> execute(String config, SqlScript sqlScript) throws SQLException {
        if (config == null || config.trim().isEmpty()) {
            throw new SQLException("config is null");
        }

        Properties info = new Properties();
        info.setProperty("model", "inline:" + config);
        info.setProperty("lex", "JAVA");


        try (Connection conn = DriverManager.getConnection("jdbc:calcite:", info)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            try (ResultSet resultSet = stmt.executeQuery(sqlScript.sql())) {
                return ResultSetUtils.resultSetToList(resultSet);
            }
        }
    }
}
