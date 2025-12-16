package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.utils.ResultSetUtils;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class Calcite {

    private String context;

    public void save(String context) {
        this.context = context;
    }

    public List<RowMap> execute(String sql) throws SQLException {
        if (context == null || context.trim().isEmpty()){
            throw new SQLException("context is null");
        }

        Properties info = new Properties();
        info.setProperty("model", "inline:" + context);

        try (Connection conn = DriverManager.getConnection("jdbc:calcite:",info)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            try(ResultSet resultSet = stmt.executeQuery(sql)){
                return ResultSetUtils.resultSetToList(resultSet);
            }
        }
    }
}
