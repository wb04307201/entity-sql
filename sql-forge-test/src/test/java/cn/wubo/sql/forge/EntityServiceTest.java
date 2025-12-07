package cn.wubo.sql.forge;

import cn.wubo.sql.forge.entity.EntitySelect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class EntityServiceTest {

    @Autowired
    private EntityService entityService;

    @Test
    void test() throws Exception {
        EntitySelect<User> select = Entity.select(User.class);
        List<User> users = entityService.run(select);
        log.info("{}",users);
    }


}
