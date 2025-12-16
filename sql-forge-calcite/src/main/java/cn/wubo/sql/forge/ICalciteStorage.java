package cn.wubo.sql.forge;

public interface ICalciteStorage<T extends ApiTemplate> extends IApiTemplateStorage<T> {

    String getComfig();
    void saveConfig(String config);
}
