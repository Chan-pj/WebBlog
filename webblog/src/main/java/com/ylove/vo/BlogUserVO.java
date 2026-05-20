package com.ylove.vo;

public class BlogUserVO {
    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private boolean approved;
    private boolean canCreateBlog;
    private boolean canCreateBoard;
    private int boardCreateQuota;
    private int imageUploadLimitCount;
    private long imageSizeLimitBytes;
    private boolean managementBlocked;
    private boolean profileRestricted;
    private String phoneNumber;
    private String profileImageUrl;
    private String profileBlogTitle;
    private String profileEtc;
    private boolean useKakaoAuth;
    private String kakaoId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isCanCreateBlog() {
        return canCreateBlog;
    }

    public void setCanCreateBlog(boolean canCreateBlog) {
        this.canCreateBlog = canCreateBlog;
    }

    public boolean isCanCreateBoard() {
        return canCreateBoard;
    }

    public void setCanCreateBoard(boolean canCreateBoard) {
        this.canCreateBoard = canCreateBoard;
    }

    public int getBoardCreateQuota() {
        return boardCreateQuota;
    }

    public void setBoardCreateQuota(int boardCreateQuota) {
        this.boardCreateQuota = boardCreateQuota;
    }

    public int getImageUploadLimitCount() {
        return imageUploadLimitCount;
    }

    public void setImageUploadLimitCount(int imageUploadLimitCount) {
        this.imageUploadLimitCount = imageUploadLimitCount;
    }

    public long getImageSizeLimitBytes() {
        return imageSizeLimitBytes;
    }

    public void setImageSizeLimitBytes(long imageSizeLimitBytes) {
        this.imageSizeLimitBytes = imageSizeLimitBytes;
    }

    public boolean isManagementBlocked() {
        return managementBlocked;
    }

    public void setManagementBlocked(boolean managementBlocked) {
        this.managementBlocked = managementBlocked;
    }

    public boolean isProfileRestricted() {
        return profileRestricted;
    }

    public void setProfileRestricted(boolean profileRestricted) {
        this.profileRestricted = profileRestricted;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileBlogTitle() {
        return profileBlogTitle;
    }

    public void setProfileBlogTitle(String profileBlogTitle) {
        this.profileBlogTitle = profileBlogTitle;
    }

    public String getProfileEtc() {
        return profileEtc;
    }

    public void setProfileEtc(String profileEtc) {
        this.profileEtc = profileEtc;
    }

    public boolean isUseKakaoAuth() {
        return useKakaoAuth;
    }

    public void setUseKakaoAuth(boolean useKakaoAuth) {
        this.useKakaoAuth = useKakaoAuth;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    @Override
    public String toString() {
        return "BlogUserVO{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", role='" + role + '\'' +
                ", approved=" + approved +
                ", canCreateBlog=" + canCreateBlog +
                ", canCreateBoard=" + canCreateBoard +
                ", boardCreateQuota=" + boardCreateQuota +
                ", imageUploadLimitCount=" + imageUploadLimitCount +
                ", imageSizeLimitBytes=" + imageSizeLimitBytes +
                ", managementBlocked=" + managementBlocked +
                ", profileRestricted=" + profileRestricted +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", profileBlogTitle='" + profileBlogTitle + '\'' +
                ", profileEtc='" + profileEtc + '\'' +
                ", useKakaoAuth=" + useKakaoAuth +
                ", kakaoId='" + kakaoId + '\'' +
                '}';
    }
}