package cn.wubo.sql.forge;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiTemplateStorage implements IApiTemplateStorage<ApiTemplate> {

    private static final List<ApiTemplate> apiTemplateList = new ArrayList<>();

    @Override
    public void save(ApiTemplate apiTemplate) {
        Optional<ApiTemplate> existingMetaData = apiTemplateList.stream()
                .filter(metaData -> metaData.getId().equals(apiTemplate.getId()))
                .findFirst();
        if (existingMetaData.isPresent()) {
            int index = apiTemplateList.indexOf(existingMetaData.get());
            apiTemplateList.set(index, apiTemplate);
        } else {
            apiTemplateList.add(apiTemplate);
        }
    }

    @Override
    public ApiTemplate get(String id) {
        Optional<ApiTemplate> existingMetaData = apiTemplateList.stream()
                .filter(metaData -> metaData.getId().equals(id))
                .findFirst();
        if (existingMetaData.isPresent())
            return existingMetaData.get();
        else
            throw new IllegalArgumentException("apiTemplate not found");
    }

    @Override
    public void remove(String id) {
        Optional<ApiTemplate> existingMetaData = apiTemplateList.stream()
                .filter(metaData -> metaData.getId().equals(id))
                .findFirst();
        if (existingMetaData.isPresent()) {
            apiTemplateList.remove(existingMetaData.get());
        } else {
            throw new IllegalArgumentException("apiTemplate not found");
        }
    }

    @Override
    public List<ApiTemplate> list() {
        return apiTemplateList;
    }
}
