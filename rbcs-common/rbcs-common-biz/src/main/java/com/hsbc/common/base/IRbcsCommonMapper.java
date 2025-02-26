package com.hsbc.common.base;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IRbcsCommonMapper {

    @Insert("insert into ${tableName}_his select * from ${tableName} where trace_id = #{traceId}")
    int insertHis(@Param("tableName") String tableName, @Param("traceId") Long traceId);

}
