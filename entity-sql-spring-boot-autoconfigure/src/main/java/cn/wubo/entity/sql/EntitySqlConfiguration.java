package cn.wubo.entity.sql;

import cn.wubo.entity.sql.utils.FreemarkerUtils;
import cn.wubo.entity.sql.web.EntityWebService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
//@ConditionalOnBean(DataSource.class)
public class EntitySqlConfiguration {

    @Bean
    public DataSourceHelper dataSourceHelper(DataSource dataSource) {
        return new DataSourceHelper(dataSource);
    }

    @Bean
    public EntityWebService entityWebService(DataSourceHelper dataSourceHelper) {
        return new EntityWebService(dataSourceHelper);
    }

    @Bean("EntitySqlWebRouter")
    public RouterFunction<ServerResponse> entitySqlWebRouter(EntityWebService entityWebService) {
        return RouterFunctions.route().GET("/entity/view/{id}", request -> {
                    String id = request.pathVariable("id");
                    String contextPath = request.requestPath().contextPath().value();
                    return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(entityWebService.view(id, contextPath));
                }).POST("/entity/select/{id}", request -> {
                    String id = request.pathVariable("id");
                    Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
                    });
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Result.success(entityWebService.select(id, params)));
                }).POST("/entity/save/{id}", request -> {
                    String id = request.pathVariable("id");
                    Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
                    });
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Result.success(entityWebService.save(id, params)));
                })
                .POST("/entity/getById/{id}", request -> {
                    String id = request.pathVariable("id");
                    Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
                    });
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Result.success(entityWebService.getById(id, params)));
                })
                .POST("/entity/deleteByIds/{id}", request -> {
                    String id = request.pathVariable("id");
                    Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
                    });
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Result.success(entityWebService.deleteByIds(id, params)));
                })
                .build();
    }
}
