package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.utils.ResultSetUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.stylesheets.LinkStyle;

import java.sql.*;
import java.util.List;
import java.util.Properties;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class CalciteTest {

    @Test
    void testModel() throws SQLException {
        Properties info = new Properties();
        info.setProperty("model", "./sql/model.json");
        info.setProperty("lex", "JAVA");

        String sql = """
select student.name, sum(score.grade) as grade 
from MYSQL.student as student join POSTGRES.score as score on student.id=score.student_id 
where student.id>0 
group by student.name
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:calcite:",info)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            try(ResultSet resultSet = stmt.executeQuery(sql)){
                List<RowMap> list = ResultSetUtils.resultSetToList(resultSet);
                log.info("{}",list);
            }
        }
    }

    @Test
    void testInline() throws SQLException {

        String config = """
{
  "version": "1.0",
  "defaultSchema": "MYSQL",
  "schemas": [
    {
      "factory": "org.apache.calcite.adapter.jdbc.JdbcSchema$Factory",
      "name": "MYSQL",
      "operand": {
        "jdbcDriver": "com.mysql.cj.jdbc.Driver",
        "jdbcUrl": "jdbc:mysql://localhost:3306/test",
        "jdbcUser": "root",
        "jdbcPassword": "123456"
      },
      "type": "custom"
    },
    {
      "factory": "org.apache.calcite.adapter.jdbc.JdbcSchema$Factory",
      "name": "POSTGRES",
      "operand": {
        "jdbcDriver": "org.postgresql.Driver",
        "jdbcUrl": "jdbc:postgresql://localhost:5432/test",
        "jdbcUser": "postgres",
        "jdbcPassword": "123456"
      },
      "type": "custom"
    }
  ]
}
                """;

        Properties info = new Properties();
        info.setProperty("model", "inline:" + config);
        info.setProperty("lex", "JAVA");

        String sql = """
select student.name, sum(score.grade) as grade 
from MYSQL.student as student join POSTGRES.score as score on student.id=score.student_id 
where student.id>0 
group by student.name
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:calcite:",info)) {
            // 查询数据
            Statement stmt = conn.createStatement();
            try(ResultSet resultSet = stmt.executeQuery(sql)){
                List<RowMap> list = ResultSetUtils.resultSetToList(resultSet);
                log.info("{}",list);
            }
        }
    }
}
