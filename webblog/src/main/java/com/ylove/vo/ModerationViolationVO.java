package com.ylove.vo;

public class ModerationViolationVO {
    private Long violationId;
    private Long blogId;
    private Long boardId;
    private Long userId;
    private String writerIp;
    private String violationType;
    private String matchedWord;
    private String attemptedTitle;
    private String attemptedSnippet;
    private String createdAt;
    private String userNickname;
    private String userEmail;
    private String blogName;

    public Long getViolationId() {
        return violationId;
    }

    public void setViolationId(Long violationId) {
        this.violationId = violationId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWriterIp() {
        return writerIp;
    }

    public void setWriterIp(String writerIp) {
        this.writerIp = writerIp;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getMatchedWord() {
        return matchedWord;
    }

    public void setMatchedWord(String matchedWord) {
        this.matchedWord = matchedWord;
    }

    public String getAttemptedTitle() {
        return attemptedTitle;
    }

    public void setAttemptedTitle(String attemptedTitle) {
        this.attemptedTitle = attemptedTitle;
    }

    public String getAttemptedSnippet() {
        return attemptedSnippet;
    }

    public void setAttemptedSnippet(String attemptedSnippet) {
        this.attemptedSnippet = attemptedSnippet;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
}