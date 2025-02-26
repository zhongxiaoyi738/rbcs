package com.hsbc.common.base;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IRbcsBaseService <T extends RbcsBaseEntity> extends IService<T> {

    int insert(T entity);

    int update(T entity);

    int updateWrapper(T entity, LambdaUpdateWrapper<T> lambdaUpdateWrapper);

    int delete(T entity);

    int deleteWrapper(T entity, LambdaUpdateWrapper<T> lambdaUpdateWrapper);
}
