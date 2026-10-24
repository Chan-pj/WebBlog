package com.chan.service;
import java.util.List;
import com.chan.vo.BlogMemberVO;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;
public interface BlogService {
    BlogVO createDefaultBlog(Long ownerUserId, String nickname);
    BlogVO getBlogById(Long blogId);
    BlogVO getBlogByOwnerUserId(Long ownerUserId);
    List<BlogVO> getAllBlogs(int offset, int limit);
    int countAllBlogs();
    void updateBlogSetting(BlogVO blog);
    void deleteBlog(Long blogId); // ← 추가
    void addMember(BlogMemberVO member);
    List<BlogMemberVO> getMembers(Long blogId);
    void changeMemberStatus(BlogMemberVO member);
    Long resolveLandingBlogId(Long userId);
    Long findActiveMemberBlogId(Long userId);
    boolean canWritePost(Long blogId, Long userId);
    boolean isBlogOwner(Long blogId, Long userId);
    List<BlogVO> listPublicBlogs(int offset, int limit);
    int countPublicBlogs();
    void joinBlog(Long blogId, Long userId);
    BlogVO createOwnedBlog(Long ownerUserId, String blogName, BlogUserVO actingUser);
    boolean canReadBlog(Long blogId, Long userIdOrNull);
}