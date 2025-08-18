package cn.wubo.entity.sql.cache;

import cn.wubo.cache.CHMCache;
import cn.wubo.entity.sql.core.model.TableModel;

public class CacheHelper {

    public static CHMCache<Class<?>, TableModel> tableModelCache = new CHMCache<>(1000, 8 * 60 * 60_000);

}
