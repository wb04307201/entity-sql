package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.records.SqlScript;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import javax.sql.DataSource;

import java.net.URI;
import java.util.Map;

import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@EnableCaching
@AutoConfiguration
public class SqlForgeConfiguration {

    @Bean
    public FunctionalState functionalState() {
        return new FunctionalState();
    }

    @Bean
    public Executor executor(DataSource dataSource) {
        return new Executor(dataSource);
    }

    @Bean
    public MetaData metaData(DataSource dataSource) {
        return new MetaData(dataSource);
    }

    @Bean
    public CrudService crudService(Executor executor) {
        return new CrudService(executor);
    }

    @Bean
    public CacheService cacheService() {
        return new CacheService();
    }

    @Bean
    public EntityService entityService(CrudService crudService, CacheService cacheService) {
        return new EntityService(crudService, cacheService);
    }

    @Bean("sqlForgeApiJsonRouter")
    @ConditionalOnProperty(name = "sql.forge.api.json.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiRouter(FunctionalState functionalState, CrudService crudService) {
        functionalState.setApiJson(true);
        RouterFunctions.Builder builder = route();
        builder.POST("sql/forge/api/json/{method}/{tableName}", accept(MediaType.APPLICATION_JSON), request -> {
            String method = request.pathVariable("method");
            String tableName = request.pathVariable("tableName");
            Object obj = switch (method) {
                case "delete" -> crudService.delete(tableName, request.body(Delete.class));
                case "insert" -> crudService.insert(tableName, request.body(Insert.class));
                case "select" -> crudService.select(tableName, request.body(Select.class));
                case "update" -> crudService.update(tableName, request.body(Update.class));
                default -> throw new IllegalArgumentException("method not found");
            };
            return ServerResponse.ok().body(obj);
        });
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public IApiTemplateStorage apiTemplateStorage() {
        return new ApiTemplateStorage();
    }

    @Bean("sqlForgeApiTemplateRouter")
    @ConditionalOnProperty(name = "sql.forge.api.template.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiTemplateRouter(FunctionalState functionalState, IApiTemplateStorage apiTemplateStorage, Executor executor) {
        functionalState.setApiTemplate(true);
        RouterFunctions.Builder builder = route();
        builder.POST("sql/forge/api/template", accept(MediaType.APPLICATION_JSON), request -> {
            ApiTemplate apiTemplate = request.body(ApiTemplate.class);
            apiTemplateStorage.save(apiTemplate);
            return ServerResponse.ok().body(true);
        });
        builder.GET("sql/forge/api/template", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.param("id").orElse(null);
            return ServerResponse.ok().body(apiTemplateStorage.get(id));
        });
        builder.POST("sql/forge/api/template/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            ApiTemplate apiTemplate = apiTemplateStorage.get(id);
            Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
            });

            SqlTemplateEngine engine = new SqlTemplateEngine();
            SqlScript result = engine.process(apiTemplate.getContext(), params);
            return ServerResponse.ok().body(executor.execute(result));
        });
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "sql.forge.api.database.enabled", havingValue = "true", matchIfMissing = true)
    public CurrentMetaData currentMetaData(MetaData metaData) {
        return new CurrentMetaData(metaData);
    }

    @Bean("sqlForgeApiDatabaseRouter")
    @ConditionalOnProperty(name = "sql.forge.api.database.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiDatabaseRouter(FunctionalState functionalState, Executor executor, CurrentMetaData currentMetaData) {
        functionalState.setApiDatabase(true);
        RouterFunctions.Builder builder = route();
        builder.GET("/sql/forge/api/database/current", request -> ServerResponse.ok().body(currentMetaData.getCurrentDatabase()));
        builder.POST("/sql/forge/api/database/current/execute", request -> {
            SqlScript sqlScript = request.body(SqlScript.class);
            return ServerResponse.ok().body(executor.execute(sqlScript));
        });
        return builder.build();
    }

    @Bean("sqlForgeConsoleRouter")
    @ConditionalOnProperty(name = "sql.forge.console.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeConsoleRouter(FunctionalState functionalState) {
        RouterFunctions.Builder builder = RouterFunctions.route();
        builder.GET("/sql/forge/console", request -> ServerResponse.temporaryRedirect(URI.create("/sql/forge/console/index.html")).build());
        builder.GET("/sql/forge/console/", request -> ServerResponse.temporaryRedirect(URI.create("/sql/forge/console/index.html")).build());
        builder.GET("/sql/forge/console/functionalState", request -> ServerResponse.ok().body(functionalState.getFunctionalState()));
        return builder.build();
    }
}
