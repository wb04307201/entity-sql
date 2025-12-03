package cn.wubo.sql.forge;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import javax.sql.DataSource;

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

    @Bean("sqlForgeRouter")
    public RouterFunction<ServerResponse> sqlForgeRouter(MetaData metaData) {
        RouterFunctions.Builder builder = RouterFunctions.route();
        builder.GET("/sql/forge/database", request -> ServerResponse.ok().body(metaData.getDatabase()));
        builder.GET("/sql/forge/database/catalogs", request -> ServerResponse.ok().body(metaData.getCatalogs()));
        builder.GET("/sql/forge/schemas", request -> {
            String catalog = request.param("catalog").orElse(null);
            String schemaPattern = request.param("schemaPattern").orElse(null);
            return ServerResponse.ok().body(metaData.getSchemas(catalog, schemaPattern));
        });
        builder.GET("/sql/forge/tables", request -> {
            String catalog = request.param("catalog").orElse(null);
            String schemaPattern = request.param("schemaPattern").orElse(null);
            String tableNamePattern = request.param("tableNamePattern").orElse(null);
            String[] types = request.param("types").map(typesStr -> typesStr.split(",")).orElse(null);
            return ServerResponse.ok().body(metaData.getTables(catalog, schemaPattern, tableNamePattern, types));
        });
        return builder.build();
    }

}
