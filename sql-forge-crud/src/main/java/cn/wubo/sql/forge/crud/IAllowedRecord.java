package cn.wubo.sql.forge.crud;

public sealed interface IAllowedRecord permits Delete, Insert, Select, SelectPage, Update {
}
