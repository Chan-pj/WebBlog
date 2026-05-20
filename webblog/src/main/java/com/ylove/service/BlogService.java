package com.ylove.service;

import java.util.List;

import com.ylove.vo.BlogMemberVO;
import com.ylove.vo.BlogUserVO;
import com.ylove.vo.BlogVO;

public interface BlogService {
    BlogVO createDefaultBlog(Long ownerUserId, String nickname);

    BlogVO getBlogById(Long blogId);

    BlogVO getBlogByOwnerUserId(Long ownerUserId);

    List<BlogVO> getAllBlogs();

    void updateBlogSetting(BlogVO blog);

    void addMember(BlogMemberVO member);

    List<BlogMemberVO> getMembers(Long blogId);

    void changeMemberStatus(BlogMemberVO member);

    /**
     * 로그인 후 이동할 블로그 ID. 소유 블로그가 있으면 우선, 없으면 활성 멤버십이 있는 블로그.
     */
    Long resolveLandingBlogId(Long userId);

    /**
     * 소유 블로그 없이 멤버로만 참여 중인 경우의 활성 블로그 ID(없으면 null).
     */
    Long findActiveMemberBlogId(Long userId);

    /**
     * 사용자가 해당 블로그에서 글쓰기 가능한지(소유자 또는 ACTIVE 멤버) 확인.
     */
    boolean canWritePost(Long blogId, Long userId);

    /**
     * 블로그 소유자(관리자) 여부.
     */
    boolean isBlogOwner(Long blogId, Long userId);

    /**
     * 공개 블로그 목록(멤버 가입 탐색용).
     */
    List<BlogVO> listPublicBlogs();

    /**
     * 공개 블로그 멤버 가입(또는 비활성 멤버 재활성).
     */
    void joinBlog(Long blogId, Long userId);

    /**
     * 승인·권한을 갖춘 블로그 관리자가 자신의 블로그를 개설하고 기본 게시판을 만듭니다.
     */
    BlogVO createOwnedBlog(Long ownerUserId, String blogName, BlogUserVO actingUser);

    /**
     * 비공개 블로그는 소유자·활성 멤버만 조회 가능. 공개는 모두 조회 가능.
     */
    boolean canReadBlog(Long blogId, Long userIdOrNull);
}
