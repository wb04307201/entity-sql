package cn.wubo.sql.forge;

public interface IFunctionValue {

    boolean support(String functionName);

    Object transValue(String functionName, Object value);
}
