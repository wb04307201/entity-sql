package cn.wubo.sql.forge.bulid.segment;

public interface IPage<Children> {

    Children page(int page, int pageSize);
}
