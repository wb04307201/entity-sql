package cn.wubo.entity.sql.exception;

public class EntityWebException extends RuntimeException {

    public EntityWebException(String message) {
        super(message);
    }

    public EntityWebException(String message, Throwable cause) {
        super(message, cause);
    }
}
