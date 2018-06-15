package com.system.cron.util.mybatis;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用Mapper接口使用
 * Created by ctianlong on 2018/5/23.
 * 特别注意，该接口不能被扫描到，否则会出错
 */
@RegisterMapper
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T>
{
}
