package cn.wubo.entity.sql.core.segment;

public interface IPage<Children> {

    Children page(int page, int pageSize);
}
