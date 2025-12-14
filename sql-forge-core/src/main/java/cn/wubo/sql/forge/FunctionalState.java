package cn.wubo.sql.forge;

public class FunctionalState {

    private Boolean apiJson = Boolean.FALSE;
    private Boolean apiTemplate = Boolean.FALSE;
    private Boolean console = Boolean.FALSE;

    public void setApiJson(Boolean apiJson) {
        this.apiJson = apiJson;
    }

    public void setApiTemplate(Boolean apiTemplate) {
        this.apiTemplate = apiTemplate;
    }

    public void setConsole(Boolean console) {
        this.console = console;
    }
}
