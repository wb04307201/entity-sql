package cn.wubo.sql.forge.map;

import java.util.LinkedHashMap;

public class RowMap extends LinkedHashMap<String,Object> {

    public RowMap(int columnCount) {
        super(columnCount);
    }
}
