package com.chan.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void validateImageFiles(List<MultipartFile> files, long maxFileSize, int maxCount);
}