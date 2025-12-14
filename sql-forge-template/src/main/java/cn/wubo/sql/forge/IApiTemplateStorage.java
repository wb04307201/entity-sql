package cn.wubo.sql.forge;


public interface IApiTemplateStorage<T extends ApiTemplate> {
    void save(T apiTemplate);
    T get(String id);
    void remove(String id);
}
