package com.ylove.vo;

/**
 * 관리자 통합 검색 결과 한 행.
 */
public class AdminSearchResultVO {
    private String resultType;
    private Long userId;
    private String userNickname;
    private String userEmail;
    private Long blogId;
    private String blogName;
    private Long postId;
    private Long boardId;
    private String title;
    private String snippet;
    private String matchedWord;
    private String writerIp;
    private String createdAt;
    private String detailUrl;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getMatchedWord() {
        return matchedWord;
    }

    public void setMatchedWord(String matchedWord) {
        this.matchedWord = matchedWord;
    }

    public String getWriterIp() {
        return writerIp;
    }

    public void setWriterIp(String writerIp) {
        this.writerIp = writerIp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}