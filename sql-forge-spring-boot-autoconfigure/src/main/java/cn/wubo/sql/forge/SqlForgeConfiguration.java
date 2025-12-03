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
    public RouterFunction<ServerResponse> sqlForgeRouter() {
        RouterFunctions.Builder builder =RouterFunctions.route();
        return builder.build();
    }

}
