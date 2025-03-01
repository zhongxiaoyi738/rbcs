package com.hsbc.common.base;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hsbc.log.enums.FeignRequestHeaderEnum;
import com.hsbc.log.idworker.IdWorkerUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Optional;


@Slf4j
@Getter
public abstract class RbcsBaseServiceImpl <M extends BaseMapper<T>, T extends RbcsBaseEntity> extends ServiceImpl<M, T> implements IRbcsBaseService<T>{

    @Resource
    private IRbcsCommonMapper commonMapper;

    /**
     * 表示操作失败的静态常量。
     */
    protected static final int FAIL = -1;


    @Override
    public int insert(T entity) {
        setBaseFields(entity, true);
        String tableName = getTableName(entity);
        if (save(entity)) {
            // 如果保存成功，插入历史记录
            return commonMapper.insertHis(tableName, entity.getTraceId());
        }
        return FAIL;
    }

    @Override
    public int update(T entity) {
        setBaseFields(entity, false);
        String tableName = getTableName(entity);
        if(updateById(entity)) {
            return commonMapper.insertHis(tableName, entity.getTraceId());
        }
        return FAIL;
    }

    @Override
    public int updateWrapper(T entity, LambdaUpdateWrapper<T> lambdaUpdateWrapper) {
        setBaseFields(entity, false);
        String tableName = getTableName(entity);
        lambdaUpdateWrapper.set(T::getTraceId, entity.getTraceId());
        lambdaUpdateWrapper.set(T::getUpdatedBy, entity.getUpdatedBy());
        lambdaUpdateWrapper.eq(ObjectUtils.isNotNull(entity.getId()), T::getId, entity.getId());
        if (update(lambdaUpdateWrapper)) {
            return commonMapper.insertHis(tableName, entity.getTraceId());
        }
        return FAIL;
    }

    @Override
    public int delete(T entity) {
        setBaseFields(entity, false);
        String tableName = getTableName(entity);
        if(updateById(entity)) {
            commonMapper.insertHis(tableName, entity.getTraceId());
            return baseMapper.deleteById(entity);
        }
        return FAIL;
    }

    @Override
    public int deleteWrapper(T entity, LambdaUpdateWrapper<T> lambdaUpdateWrapper) {
        updateWrapper(entity, lambdaUpdateWrapper);
        return deleteWrapper(entity, lambdaUpdateWrapper);
    }


    private <T> String getTableName(T entity) {
        Class<?> clazz = entity.getClass();
        TableName annotation = clazz.getAnnotation(TableName.class);
        return annotation.value();
    }

    private <T extends RbcsBaseEntity> void setBaseFields(T entity, boolean isAdd) {
        if (isAdd) {
            entity.setCreatedBy(getUserAccountName());
            entity.setUpdatedBy(getUserAccountName());
            entity.setCreatedTime(null);
            entity.setUpdatedTime(null);
            if (ObjectUtils.isEmpty(entity.getUuid())) {
                entity.setUuid(IdWorkerUtils.nextId());
            }
        } else {
            entity.setUpdatedBy(getUserAccountName());
            entity.setUpdatedTime(null);
        }
        entity.setTraceId(getTraceId());
    }

    private String getUserAccountName() {
        String userAccountName = MDC.get(FeignRequestHeaderEnum.USER_ACCOUNT_NAME.getCode());
        return StringUtils.isNotEmpty(userAccountName) ? userAccountName : "unknow";
    }

    private Long getTraceId() {
        String strTraceId = MDC.get(FeignRequestHeaderEnum.TRACE_ID.getCode());
        return StringUtils.isNotEmpty(strTraceId) ? Long.valueOf(strTraceId) : IdWorkerUtils.getTraceId();
    }
}
