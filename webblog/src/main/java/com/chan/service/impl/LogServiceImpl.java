package com.chan.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.chan.log.LogMapper;
import com.chan.service.LogService;

@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    @Override
    public void log(Long userId, String eventType, String ipAddress) {
        try {
            logMapper.insertLog(userId, eventType, ipAddress);
        } catch (Exception e) {
            // 로그 실패가 메인 서비스에 영향 주지 않도록 무시
        }
    }
}