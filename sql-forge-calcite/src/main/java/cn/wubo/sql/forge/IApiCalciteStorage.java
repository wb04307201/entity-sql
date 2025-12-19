package cn.wubo.sql.forge;

import java.util.List;

public interface IApiCalciteStorage<T extends ApiTemplate,S extends ApiCalciteConfig> {

    void save(T apiTemplate);
    T get(String id);
    void remove(String id);
    List<T> list();
    S getConfig();
    void saveConfig(S config);
}
