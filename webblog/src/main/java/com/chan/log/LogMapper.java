package com.chan.log;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LogMapper {
    void insertLog(@Param("userId") Long userId,
                   @Param("eventType") String eventType,
                   @Param("ipAddress") String ipAddress);
}