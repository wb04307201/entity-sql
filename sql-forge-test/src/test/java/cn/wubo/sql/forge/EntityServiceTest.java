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
    void testEntitySelect() throws Exception {
        EntitySelect<User> select = Entity.select(User.class)
                .distinct(true)
                .columns(User::getId, User::getUsername, User::getEmail)
                .orders(User::getUsername)
                .in(User::getUsername, "alice", "bob")
                .page(0, 1);
        List<User> users = entityService.run(select);
        log.info("{}", users);
    }

    @Test
    void testEntity() throws Exception {
        User user = new User();
        user.setUsername("wb04307201");
        user.setEmail("wb04307201@gitee.com");
        user = entityService.run(Entity.save(user));
        log.info("{}", user);
        user.setEmail("wb04307201@github.com");
        user = entityService.run(Entity.save(user));
        log.info("{}", user);
        int count = entityService.run(Entity.delete(user));
        log.info("{}", count);
    }
}
