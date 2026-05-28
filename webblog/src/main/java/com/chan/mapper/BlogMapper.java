package com.chan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.chan.vo.BlogMemberVO;
import com.chan.vo.BlogVO;

@Mapper
public interface BlogMapper {
    int insertBlog(BlogVO blog);
    BlogVO selectBlogById(Long blogId);
    BlogVO selectBlogByOwnerUserId(Long ownerUserId);
    List<BlogVO> selectAllBlogs();
    int updateBlog(BlogVO blog);

    int insertBlogMember(BlogMemberVO member);
    List<BlogMemberVO> selectBlogMembers(Long blogId);
    int updateBlogMemberStatus(BlogMemberVO member);

    /**
     * 사용자가 멤버로 참여 중인 블로그 중, 활성 상태인 것의 blog_id 하나(최근 가입 순).
     */
    Long selectActiveMemberBlogId(Long userId);

    int countBlogOwnerMatch(@Param("blogId") Long blogId, @Param("userId") Long userId);

    int countActiveBlogMember(@Param("blogId") Long blogId, @Param("userId") Long userId);

    int countBlogMemberForUser(@Param("blogId") Long blogId, @Param("userId") Long userId);

    BlogMemberVO selectBlogMemberByBlogAndUser(@Param("blogId") Long blogId, @Param("userId") Long userId);

    int countAllBlogs();

    int countActiveMembers();
    
    List<BlogVO> selectPublicBlogs();
    
    int countActiveMembership(@Param("blogId") Long blogId, @Param("userId") Long userId);
    
    BlogMemberVO selectMembership(@Param("blogId") Long blogId, @Param("userId") Long userId);
}
