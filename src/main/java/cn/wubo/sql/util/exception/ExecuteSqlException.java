package cn.wubo.sql.util.exception;

public class ExecuteSqlException extends RuntimeException {
    public ExecuteSqlException(Throwable cause) {
        super(cause);
    }

    public ExecuteSqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
