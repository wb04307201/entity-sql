package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.base.Join;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.enums.JoinType;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class CrudServiceTest {

    @Autowired
    private CrudService crudService;


    @Test
    void testSelect() throws SQLException {
        Select select = new Select(
                new String[]{
                        "o.id AS order_id",
                        "u.username",
                        "p.name AS product_name",
                        "p.price",
                        "o.quantity",
                        "(p.price * o.quantity) AS total"
                },
                null,
                null,
                new ArrayList<Join>(){{
                    add(new Join(JoinType.INNER_JOIN, "users u ON o.user_id = u.id"));
                    add(new Join(JoinType.INNER_JOIN, "products p ON o.product_id = p.id"));
                }},
                null,
                null,
                false
        );


        List<RowMap> rowMapList = crudService.select("orders o",select);
        log.info("rowMapList: {}", rowMapList);
        assertEquals(rowMapList.size(), 4);
    }
}
