package cn.wubo.sql.forge;

import lombok.Data;

public class FunctionalState {

    private final FunctionalStateInfo functionalStateInfo = new FunctionalStateInfo();
    public void setApiJson(Boolean apiJson) {
        this.functionalStateInfo.setApiJson(apiJson);
    }

    public void setApiTemplate(Boolean apiTemplate) {
        this.functionalStateInfo.setApiTemplate(apiTemplate);
    }

    public void setApiDatabase(Boolean apiDatabase) {
        this.functionalStateInfo.setApiDatabase(apiDatabase);
    }

    public void setApiCalcite(Boolean apiCalcite) {
        this.functionalStateInfo.setApiCalcite(apiCalcite);
    }

    public void setAmis(Boolean amis) {
        this.functionalStateInfo.setAmis(amis);
    }

    public FunctionalStateInfo getFunctionalState(){
        return this.functionalStateInfo;
    }

    @Data
    public static class FunctionalStateInfo {
        private Boolean apiDatabase = Boolean.FALSE;
        private Boolean apiJson = Boolean.FALSE;
        private Boolean apiTemplate = Boolean.FALSE;
        private Boolean apiCalcite = Boolean.FALSE;
        private Boolean amis = Boolean.FALSE;
    }
}
