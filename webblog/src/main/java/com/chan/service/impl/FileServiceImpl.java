package com.chan.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chan.service.FileService;

@Service("fileService")
public class FileServiceImpl implements FileService {

    @Override
    public void validateImageFiles(List<MultipartFile> files, long maxFileSize, int maxCount) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > maxCount) {
            throw new IllegalArgumentException("업로드 파일은 최대 " + maxCount + "개까지 가능합니다.");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            if (file.getSize() > maxFileSize) {
                throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
            }
        }
    }
}