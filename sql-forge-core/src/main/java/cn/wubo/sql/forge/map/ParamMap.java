package cn.wubo.sql.forge.map;

import java.util.HashMap;

public class ParamMap extends HashMap<Integer,Object> {

    public void put(Object value) {
        super.put(this.size() + 1, value);
    }
}
