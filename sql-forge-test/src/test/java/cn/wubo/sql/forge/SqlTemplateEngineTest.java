package cn.wubo.sql.forge;

import cn.wubo.sql.forge.records.SqlScript;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
public class SqlTemplateEngineTest {

    @Test
    void test() {

        String template = """
                SELECT * FROM users
                <if test="name != null && name != ''">WHERE name = #{name}</if>
                <if test="name == null || name == ''">WHERE 0=1</if>
                <if test="ids != null && !ids.isEmpty()"><foreach collection="ids" item="id" open="AND id IN (" separator="," close=")">#{id}</foreach></if> 
                ORDER BY create_time DESC
                """;


        Map<String, Object> input = new HashMap<>();
        input.put("name", "John");
        input.put("ids", Arrays.asList(101, 102, 103));

        SqlTemplateEngine engine = new SqlTemplateEngine();
        SqlScript result = engine.process(template, input);

        assertEquals("""
                SELECT * FROM users
                WHERE name = ?

                AND id IN (?,?,?)
                ORDER BY create_time DESC
                """, result.sql());
        assertEquals("{1=John, 2=101, 3=102, 4=103}",result.params().toString());

        input.clear();
        input.put("name", null);
        input.put("ids", null);
        SqlScript result2 = engine.process(template, input);
        assertEquals("""
                SELECT * FROM users

                WHERE 0=1

                ORDER BY create_time DESC
                """, result2.sql());
        assertEquals("{}", result2.params().toString());


        input.clear();
        input.put("name", "");
        input.put("ids", Collections.emptyList());
        SqlScript result3 = engine.process(template, input);
        assertEquals("""
                SELECT * FROM users

                WHERE 0=1

                ORDER BY create_time DESC
                """, result3.sql());
        assertEquals("{}", result3.params().toString());

    }
}
