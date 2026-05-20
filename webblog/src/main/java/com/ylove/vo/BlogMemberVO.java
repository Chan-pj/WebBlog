package com.ylove.vo;

public class BlogMemberVO {
    private Long memberId;
    private Long blogId;
    private Long userId;
    private String memberRole;
    private String status;
    private String joinedAt;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "BlogMemberVO{" +
                "memberId=" + memberId +
                ", blogId=" + blogId +
                ", userId=" + userId +
                ", memberRole='" + memberRole + '\'' +
                ", status='" + status + '\'' +
                ", joinedAt='" + joinedAt + '\'' +
                '}';
    }
}