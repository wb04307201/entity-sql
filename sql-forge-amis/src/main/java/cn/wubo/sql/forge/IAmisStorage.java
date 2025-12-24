package cn.wubo.sql.forge;

import java.util.List;

public interface IAmisStorage<T extends ApiTemplate> {

    void save(T apiTemplate);
    T get(String id);
    void remove(String id);
    List<T> list();
}
