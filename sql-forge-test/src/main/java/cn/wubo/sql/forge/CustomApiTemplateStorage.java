package cn.wubo.sql.forge;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomApiTemplateStorage implements IApiTemplateStorage<ApiTemplate> {

    private EntityService entityService;

    public CustomApiTemplateStorage(EntityService entityService) {
        this.entityService = entityService;
    }

    @Override
    public void save(ApiTemplate apiTemplate) {
        try {
            List list = entityService.run(Entity.select(SqlForgeTemplate.class).eq(SqlForgeTemplate::getTemplateType, "ApiTemplate").eq(SqlForgeTemplate::getId, apiTemplate.getId()));
            if (list.isEmpty()) {
                entityService.run(Entity.insert(SqlForgeTemplate.class).set(SqlForgeTemplate::getTemplateType, "ApiTemplate").set(SqlForgeTemplate::getId, apiTemplate.getId()).set(SqlForgeTemplate::getContext, apiTemplate.getContext()));
            } else {
                entityService.run(Entity.update(SqlForgeTemplate.class).set(SqlForgeTemplate::getTemplateType, "ApiTemplate").eq(SqlForgeTemplate::getId, apiTemplate.getId()).set(SqlForgeTemplate::getContext, apiTemplate.getContext()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ApiTemplate get(String id) {
        try {
            List<SqlForgeTemplate> list = entityService.run(Entity.select(SqlForgeTemplate.class).eq(SqlForgeTemplate::getTemplateType, "ApiTemplate").eq(SqlForgeTemplate::getId, id));
            if (list.isEmpty()) {
                throw new RuntimeException("apiTemplate not found");
            } else {
                SqlForgeTemplate sqlForgeTemplate = list.get(0);
                ApiTemplate apiTemplate = new ApiTemplate();
                apiTemplate.setId(sqlForgeTemplate.getId());
                apiTemplate.setContext(sqlForgeTemplate.getContext());
                return apiTemplate;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(String id) {
        try {
            entityService.run(Entity.delete(SqlForgeTemplate.class).eq(SqlForgeTemplate::getTemplateType, "ApiTemplate").eq(SqlForgeTemplate::getId, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ApiTemplate> list() {
        try {
            return entityService.run(Entity.select(SqlForgeTemplate.class).eq(SqlForgeTemplate::getTemplateType, "ApiTemplate"))
                    .stream()
                    .map(sqlForgeTemplate -> {
                        ApiTemplate apiTemplate = new ApiTemplate();
                        apiTemplate.setId(sqlForgeTemplate.getId());
                        apiTemplate.setContext(sqlForgeTemplate.getContext());
                        return apiTemplate;
                    }).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
