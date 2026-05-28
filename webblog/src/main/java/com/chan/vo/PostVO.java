package com.chan.vo;

public class PostVO {
    private Long postId;
    private Long blogId;
    private Long boardId;
    private Long parentPostId;
    private Long writerUserId;
    private String title;
    private String content;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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

    public Long getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(Long parentPostId) {
        this.parentPostId = parentPostId;
    }

    public Long getWriterUserId() {
        return writerUserId;
    }

    public void setWriterUserId(Long writerUserId) {
        this.writerUserId = writerUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PostVO{" +
                "postId=" + postId +
                ", blogId=" + blogId +
                ", boardId=" + boardId +
                ", parentPostId=" + parentPostId +
                ", writerUserId=" + writerUserId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}