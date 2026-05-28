package com.chan.service;

public interface LogService {
    void log(Long userId, String eventType, String ipAddress);
}