package cn.wubo.sql.forge;

import java.sql.*;
import java.util.Properties;

public class Calcite {

    public void executor(String sql) throws SQLException {
        Properties properties = new Properties();

        try (Connection conn = DriverManager.getConnection("jdbc:calcite:",properties)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        }
    }
}
