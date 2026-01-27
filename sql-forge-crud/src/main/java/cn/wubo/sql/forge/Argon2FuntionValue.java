package cn.wubo.sql.forge;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2FuntionValue implements IFunctionValue {
    @Override
    public boolean support(String functionName) {
        return "argon2".equals(functionName);
    }

    @Override
    public Object transValue(String functionName, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("argon2 function value is null");
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("argon2 function value must be a String");
        }

        Argon2 argon2 = Argon2Factory.create();
        char[] password = ((String) value).toCharArray();
        return argon2.hash(10, 65536, 1, password);
    }
}
