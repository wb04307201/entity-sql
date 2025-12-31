package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.records.SqlScript;
import cn.wubo.sql.forge.utils.CalciteExcutorUtils;
import cn.wubo.sql.forge.utils.MetaDataUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;

import java.net.URI;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

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

    @Bean("sqlForgeApiDatabaseRouter")
    @ConditionalOnProperty(name = "sql.forge.api.database.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiDatabaseRouter(Executor executor) {
        RouterFunctions.Builder builder = route();
        builder.POST("/sql/forge/api/database/current/execute", request -> {
            SqlScript sqlScript = request.body(SqlScript.class);
            return ServerResponse.ok().body(executor.execute(sqlScript));
        });
        return builder.build();
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
    @ConditionalOnProperty(name = "sql.forge.api.template.enabled", havingValue = "true", matchIfMissing = true)
    public ApiTemplateStorage apiTemplateStorage() {
        return new ApiTemplateStorage();
    }

    @Bean
    @ConditionalOnProperty(name = "sql.forge.api.template.enabled", havingValue = "true", matchIfMissing = true)
    public ApiTemplateExcutor apiTemplateExcutor(IApiTemplateStorage apiTemplateStorage, Executor executor) {
        return new ApiTemplateExcutor(apiTemplateStorage, executor);
    }

    @Bean("sqlForgeApiTemplateRouter")
    @ConditionalOnProperty(name = "sql.forge.api.template.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiTemplateRouter(FunctionalState functionalState, IApiTemplateStorage apiTemplateStorage, ApiTemplateExcutor apiTemplateExcutor) {
        functionalState.setApiTemplate(true);
        RouterFunctions.Builder builder = route();
        builder.POST("sql/forge/api/template", accept(MediaType.APPLICATION_JSON), request -> {
            ApiTemplate apiTemplate = request.body(ApiTemplate.class);
            apiTemplateStorage.save(apiTemplate);
            return ServerResponse.ok().body(true);
        });
        builder.DELETE("sql/forge/api/template/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            apiTemplateStorage.remove(id);
            return ServerResponse.ok().body(true);
        });
        builder.GET("sql/forge/api/template/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            return ServerResponse.ok().body(apiTemplateStorage.get(id));
        });
        builder.GET("sql/forge/api/template", accept(MediaType.APPLICATION_JSON), request -> ServerResponse.ok().body(apiTemplateStorage.list()));
        builder.POST("sql/forge/api/template/execute/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
            });
            return ServerResponse.ok().body(apiTemplateExcutor.execute(id, params));
        });
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "sql.forge.api.calcite.enabled", havingValue = "true", matchIfMissing = true)
    public ApiCalciteStorage apiCalciteStorage() {
        return new ApiCalciteStorage();
    }

    @Bean
    @ConditionalOnProperty(name = "sql.forge.api.calcite.enabled", havingValue = "true", matchIfMissing = true)
    public ApiCalciteExcutor apiCalciteExcutor(IApiCalciteStorage apiCalciteStorage) {
        return new ApiCalciteExcutor(apiCalciteStorage);
    }

    @Bean("sqlForgeApiCalciteRouter")
    @ConditionalOnProperty(name = "sql.forge.api.calcite.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiCalciteRouter(IApiCalciteStorage apiCalciteStorage, ApiCalciteExcutor apiCalciteExcutor) {
        RouterFunctions.Builder builder = route();
        builder.GET("sql/forge/api/calciteConfig", accept(MediaType.APPLICATION_JSON), request -> ServerResponse.ok().body(apiCalciteStorage.getConfig()));
        builder.POST("sql/forge/api/calciteConfig", accept(MediaType.APPLICATION_JSON), request -> {
            ApiCalciteConfig apiCalciteConfig = request.body(ApiCalciteConfig.class);
            apiCalciteStorage.saveConfig(apiCalciteConfig);
            return ServerResponse.ok().body(true);
        });
        builder.POST("sql/forge/api/calcite", accept(MediaType.APPLICATION_JSON), request -> {
            ApiTemplate apiTemplate = request.body(ApiTemplate.class);
            apiCalciteStorage.save(apiTemplate);
            return ServerResponse.ok().body(true);
        });
        builder.DELETE("sql/forge/api/calcite/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            apiCalciteStorage.remove(id);
            return ServerResponse.ok().body(true);
        });
        builder.GET("sql/forge/api/calcite/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            return ServerResponse.ok().body(apiCalciteStorage.get(id));
        });
        builder.GET("sql/forge/api/calcite", accept(MediaType.APPLICATION_JSON), request -> ServerResponse.ok().body(apiCalciteStorage.list()));
        builder.POST("sql/forge/api/calcite/execute/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            Map<String, Object> params = request.body(new ParameterizedTypeReference<>() {
            });
            return ServerResponse.ok().body(apiCalciteExcutor.execute(id, params));
        });
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "sql.forge.amis.enabled", havingValue = "true", matchIfMissing = true)
    public AmisStorage amisStorage() {
        return new AmisStorage();
    }


    @Bean("sqlForgeAmisRouter")
    @ConditionalOnProperty(name = "sql.forge.amis.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeAmisRouter(FunctionalState functionalState, IAmisStorage amisStorage) {
        functionalState.setAmis(true);
        RouterFunctions.Builder builder = RouterFunctions.route();
        builder.GET("sql/forge/amis", request -> {
            String redirectUrl = UriComponentsBuilder.fromPath("/sql/forge/amis/index.html")
                    .queryParams(request.params())
                    .build()
                    .toUriString();
            return ServerResponse.temporaryRedirect(URI.create(redirectUrl)).build();
        });
        builder.POST("sql/forge/amis/template", accept(MediaType.APPLICATION_JSON), request -> {
            ApiTemplate apiTemplate = request.body(ApiTemplate.class);
            amisStorage.save(apiTemplate);
            return ServerResponse.ok().body(true);
        });
        builder.DELETE("sql/forge/amis/template/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            amisStorage.remove(id);
            return ServerResponse.ok().body(true);
        });
        builder.GET("sql/forge/amis/template/{id}", accept(MediaType.APPLICATION_JSON), request -> {
            String id = request.pathVariable("id");
            return ServerResponse.ok().body(amisStorage.get(id));
        });
        builder.GET("sql/forge/amis/template", accept(MediaType.APPLICATION_JSON), request -> ServerResponse.ok().body(amisStorage.list()));
        return builder.build();
    }

    @Bean("sqlForgeApiDatabaseConsoleRouter")
    @ConditionalOnProperty(name = "sql.forge.api.calcite.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnProperty(name = "sql.forge.console.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiDatabaseConsoleRouter(FunctionalState functionalState, DataSource dataSource) {
        functionalState.setApiDatabase(true);
        RouterFunctions.Builder builder = route();
        builder.GET("sql/forge/api/databaseMetaData", request -> {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            return ServerResponse.ok().body(MetaDataUtils.getDataSourceMetaDataTree(connection));
        });
        return builder.build();
    }

    @Bean("sqlForgeApiCalciteConsoleRouter")
    @ConditionalOnProperty(name = "sql.forge.api.database.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnProperty(name = "sql.forge.console.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiCalciteConsoleRouter(FunctionalState functionalState, IApiCalciteStorage apiCalciteStorage) {
        functionalState.setApiCalcite(true);
        RouterFunctions.Builder builder = route();
        builder.GET("sql/forge/api/calciteMetaData", request -> {
            String config = apiCalciteStorage.getConfig().getContext();

            if (config == null || config.trim().isEmpty()) {
                return ServerResponse.ok().body(new DataSourceMetaDataTree());
            }

            Properties info = new Properties();
            info.setProperty("model", "inline:" + config);
            info.setProperty("lex", "JAVA");

            try (Connection conn = DriverManager.getConnection("jdbc:calcite:", info)) {
                return ServerResponse.ok().body(MetaDataUtils.getDataSourceMetaDataTree(conn));
            }
        });
        builder.POST("sql/forge/api/calcite/sql/execute", accept(MediaType.APPLICATION_JSON), request -> {
            SqlScript sqlScript = request.body(SqlScript.class);
            return ServerResponse.ok().body(CalciteExcutorUtils.execute(apiCalciteStorage.getConfig().getContext(), sqlScript));
        });
        return builder.build();
    }


    @Bean("sqlForgeConsoleRouter")
    @ConditionalOnProperty(name = "sql.forge.console.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeConsoleRouter(FunctionalState functionalState) {
        RouterFunctions.Builder builder = RouterFunctions.route();
        builder.GET("sql/forge/console", request -> ServerResponse.temporaryRedirect(URI.create("/sql/forge/console/index.html")).build());
        builder.GET("sql/forge/console/functionalState", request -> ServerResponse.ok().body(functionalState.getFunctionalState()));
        return builder.build();
    }
}
