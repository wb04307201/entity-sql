package cn.wubo.sql.forge;

import cn.wubo.sql.forge.map.RowMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ApiTemplateExcutorTest {

    @Autowired
    private CustomApiTemplateStorage apiTemplateStorage;

    @Autowired
    private ApiTemplateExcutor apiTemplateExcutor;

    @BeforeEach
    void BeforeEach() {
        log.info("ApiTemplateExcutorTest start");
        ApiTemplate apiTemplate = new ApiTemplate();
        apiTemplate.setId("test");
        apiTemplate.setContext("""
                SELECT * FROM users WHERE 1=1
                <if test="name != null && name != ''"> AND username = #{name}</if>
                <if test="ids != null && !ids.isEmpty()"><foreach collection="ids" item="id" open=" AND id IN (" separator="," close=")">#{id}</foreach></if>
                <if test="(name == null || name == '') && (ids == null || ids.isEmpty()) "> AND 0=1</if> 
                ORDER BY username DESC
                """);
        apiTemplateStorage.save(apiTemplate);
    }

    @Test
    void test() throws SQLException {
        Map<String, Object> params = Map.of("name", "alice", "ids", List.of(
                "550e8400-e29b-41d4-a716-446655440000",
                "550e8400-e29b-41d4-a716-446655440001",
                "550e8400-e29b-41d4-a716-446655440002"
        ));
        List<RowMap> rowMaps = (List<RowMap>) apiTemplateExcutor.execute("test", params);
        log.info("{}", rowMaps);
    }
}
