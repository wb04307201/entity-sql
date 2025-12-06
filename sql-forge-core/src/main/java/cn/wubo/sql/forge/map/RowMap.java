package cn.wubo.sql.forge.map;

import java.util.HashMap;

public class RowMap extends HashMap<String,Object> {

    public RowMap(int columnCount) {
        super(columnCount);
    }
}
