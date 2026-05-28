package com.chan.vo;

public class UploadFileVO {
    private Long fileId;
    private Long postId;
    private String originalName;
    private String storedName;
    private String contentType;
    private Long fileSizeBytes;
    private String filePath;
    private String createdAt;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UploadFileVO{" +
                "fileId=" + fileId +
                ", postId=" + postId +
                ", originalName='" + originalName + '\'' +
                ", storedName='" + storedName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSizeBytes=" + fileSizeBytes +
                ", filePath='" + filePath + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}