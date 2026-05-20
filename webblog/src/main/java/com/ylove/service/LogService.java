package com.ylove.service;

public interface LogService {
    void log(Long userId, String eventType, String ipAddress);
}