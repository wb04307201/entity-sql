package cn.wubo.entity.sql;

import cn.wubo.entity.sql.web.EntityWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppReadyListener implements ApplicationRunner {

    private final EntityWebService entityWebService;

    @Autowired
    public AppReadyListener(EntityWebService entityWebService) {
        this.entityWebService = entityWebService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // user 为页面访问标识，多个界面注意标识不要重复
        entityWebService.build("user", User.class);
    }
}
