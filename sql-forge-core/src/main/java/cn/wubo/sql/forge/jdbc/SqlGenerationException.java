package cn.wubo.sql.forge.jdbc;

import java.io.IOException;

public class SqlGenerationException extends RuntimeException {
    public SqlGenerationException(IOException cause) {
        super("SQL generation failed due to IO exception", cause);
    }
}

