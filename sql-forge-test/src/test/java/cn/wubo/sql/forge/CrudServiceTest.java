package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.crud.base.Join;
import cn.wubo.sql.forge.crud.base.Where;
import cn.wubo.sql.forge.enums.ConditionType;
import cn.wubo.sql.forge.enums.JoinType;
import cn.wubo.sql.forge.map.RowMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
                        "orders.id AS order_id",
                        "users.username",
                        "products.name AS product_name",
                        "products.price",
                        "orders.quantity",
                        "(products.price * orders.quantity) AS total"
                },
                null,
                null,
                new ArrayList<>() {{
                    add(new Join(JoinType.INNER_JOIN, "users","orders.user_id = users.id"));
                    add(new Join(JoinType.INNER_JOIN, "products","orders.product_id = products.id"));
                }},
                null,
                null,
                false
        );


        List<RowMap> rowMapList = crudService.select("orders", select);
        log.info("rowMapList: {}", rowMapList);
        assertEquals(4, rowMapList.size());
    }

    @Test
    void test() throws SQLException {
        String id = UUID.randomUUID().toString();
        List<Where> wheres = new ArrayList<>() {{
            add(new Where("id", ConditionType.EQ, id));
        }};
        Insert insert = new Insert(
                new HashMap<>() {{
                    put("id", id);
                    put("username", "wb04307201");
                    put("email", "wb04307201@gitee.com");
                }},
                new Select(
                        null,
                        wheres,
                        null,
                        null,
                        null,
                        null,
                        false
                )
        );

        List<RowMap> rowMapList = (List<RowMap>) crudService.insert("users", insert);
        assertEquals(1, rowMapList.size());

        Update update = new Update(
                new HashMap<>() {{
                    put("email", "wb04307201@github.com");
                }},
                wheres,
                new Select(
                        null,
                        wheres,
                        null,
                        null,
                        null,
                        null,
                        false
                )
        );

        rowMapList = (List<RowMap>) crudService.update("users", update);

        assertEquals("wb04307201@github.com", rowMapList.get(0).get("EMAIL"));

        Delete delete = new Delete(
                wheres,
                new Select(
                        null,
                        wheres,
                        null,
                        null,
                        null,
                        null,
                        false
                )
        );

        rowMapList = (List<RowMap>) crudService.delete("users", delete);
        assertEquals(0, rowMapList.size());
    }
}
