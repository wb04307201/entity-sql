package cn.wubo.sql.forge;

import cn.wubo.sql.forge.entity.base.AbstractBase;
import cn.wubo.sql.forge.entity.cache.CacheService;

public record EntityService(
        CrudService crudService,
        CacheService cacheService
) {

    /**
     * 执行抽象基类的运行方法
     *
     * @param <T>          抽象基类的泛型类型参数T
     * @param <R>          抽象基类的泛型类型参数R，表示返回值类型
     * @param <C>          抽象基类的泛型类型参数C，表示具体实现类类型，必须继承自AbstractBase
     * @param abstractBase 抽象基类实例，需要执行其run方法
     * @return 返回抽象基类run方法的执行结果，类型为R
     * @throws Exception 执行过程中可能抛出的异常
     */
    public <T, R, C extends AbstractBase<T, R, C>> R run(AbstractBase<T, R, C> abstractBase) throws Exception {
        return abstractBase.run(cacheService, crudService);
    }

}
