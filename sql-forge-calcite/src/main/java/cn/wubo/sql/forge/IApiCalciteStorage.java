package cn.wubo.sql.forge;

public interface IApiCalciteStorage<T extends ApiTemplate> extends IApiTemplateStorage<T> {

    String getComfig();
    void saveConfig(String config);
}
