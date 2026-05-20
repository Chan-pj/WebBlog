package com.ylove.vo;

public class BlogVO {
    private Long blogId;
    private Long ownerUserId;
    private String blogName;
    private boolean isPublic;
    private int boardLimit;
    private int imageLimitCount;
    private long imageSizeLimitBytes;
    private long fileSizeLimitBytes;
    private long totalCapacityBytes;

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getBoardLimit() {
        return boardLimit;
    }

    public void setBoardLimit(int boardLimit) {
        this.boardLimit = boardLimit;
    }

    public int getImageLimitCount() {
        return imageLimitCount;
    }

    public void setImageLimitCount(int imageLimitCount) {
        this.imageLimitCount = imageLimitCount;
    }

    public long getImageSizeLimitBytes() {
        return imageSizeLimitBytes;
    }

    public void setImageSizeLimitBytes(long imageSizeLimitBytes) {
        this.imageSizeLimitBytes = imageSizeLimitBytes;
    }

    public long getFileSizeLimitBytes() {
        return fileSizeLimitBytes;
    }

    public void setFileSizeLimitBytes(long fileSizeLimitBytes) {
        this.fileSizeLimitBytes = fileSizeLimitBytes;
    }

    public long getTotalCapacityBytes() {
        return totalCapacityBytes;
    }

    public void setTotalCapacityBytes(long totalCapacityBytes) {
        this.totalCapacityBytes = totalCapacityBytes;
    }

    @Override
    public String toString() {
        return "BlogVO{" +
                "blogId=" + blogId +
                ", ownerUserId=" + ownerUserId +
                ", blogName='" + blogName + '\'' +
                ", isPublic=" + isPublic +
                ", boardLimit=" + boardLimit +
                ", imageLimitCount=" + imageLimitCount +
                ", imageSizeLimitBytes=" + imageSizeLimitBytes +
                ", fileSizeLimitBytes=" + fileSizeLimitBytes +
                ", totalCapacityBytes=" + totalCapacityBytes +
                '}';
    }
}