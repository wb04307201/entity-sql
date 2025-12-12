package cn.wubo.sql.forge;

import cn.wubo.sql.forge.crud.Delete;
import cn.wubo.sql.forge.crud.Insert;
import cn.wubo.sql.forge.crud.Select;
import cn.wubo.sql.forge.crud.Update;
import cn.wubo.sql.forge.entity.cache.CacheService;
import cn.wubo.sql.forge.records.SqlScript;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import javax.sql.DataSource;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@EnableCaching
@AutoConfiguration
public class SqlForgeConfiguration {

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

    @Bean("sqlForgeApiRouter")
    @ConditionalOnProperty(name = "sql.forge.api.json.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeApiRouter(CrudService crudService) {
        RouterFunctions.Builder builder = route();
        builder.POST("/{method}/{tableName}", accept(MediaType.APPLICATION_JSON), request -> {
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

    @Bean("sqlForgeRouter")
    @ConditionalOnProperty(name = "sql.forge.console.enabled", havingValue = "true", matchIfMissing = true)
    public RouterFunction<ServerResponse> sqlForgeRouter(MetaData metaData, Executor executor) {
        RouterFunctions.Builder builder = RouterFunctions.route();
        builder.GET("/sql/forge", request -> ServerResponse.temporaryRedirect(URI.create("/sql/forge/index.html")).build());
        builder.GET("/sql/forge/", request -> ServerResponse.temporaryRedirect(URI.create("/sql/forge/index.html")).build());
        builder.GET("/sql/forge/database", request -> ServerResponse.ok().body(metaData.getDatabase()));
        builder.GET("/sql/forge/tables", request -> {
            String catalog = request.param("catalog").orElse(null);
            String schemaPattern = request.param("schemaPattern").orElse(null);
            String tableNamePattern = request.param("tableNamePattern").orElse(null);
            String[] types = request.param("types").map(typesStr -> typesStr.split(",")).orElse(null);
            return ServerResponse.ok().body(metaData.getTables(catalog, schemaPattern, tableNamePattern, types));
        });
        builder.GET("/sql/forge/columns", request -> {
            String catalog = request.param("catalog").orElse(null);
            String schemaPattern = request.param("schemaPattern").orElse(null);
            String tableNamePattern = request.param("tableNamePattern").orElse(null);
            String columnNamePattern = request.param("columnNamePattern").orElse(null);
            return ServerResponse.ok().body(metaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
        });
        builder.POST("/sql/forge/execute", request -> {
            SqlScript sqlScript =  request.body(SqlScript.class);
            return ServerResponse.ok().body(executor.execute(sqlScript));
        });
        return builder.build();
    }
}
