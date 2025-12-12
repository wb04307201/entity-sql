package cn.wubo.sql.forge.entity.cache;

import cn.wubo.sql.forge.entity.utils.ReflectionUtils;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {

    private static final Map<Class<?>, TableStructureInfo> metadataCache = new ConcurrentHashMap<>();

    @Cacheable(value = "tableStructureInfo", key = "#p0")
    public TableStructureInfo getTableInfo(Class<?> entityClass) {
        return metadataCache.computeIfAbsent(entityClass, ReflectionUtils::extractTableInfo);
    }
}
