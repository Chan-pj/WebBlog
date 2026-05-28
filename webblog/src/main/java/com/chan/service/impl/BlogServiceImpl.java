package com.chan.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chan.mapper.BlogMapper;
import com.chan.mapper.BoardMapper;
import com.chan.service.BlogService;
import com.chan.vo.BlogMemberVO;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;
import com.chan.vo.BoardVO;

@Service("blogService")
public class BlogServiceImpl implements BlogService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private BoardMapper boardMapper;

    @Override
    public BlogVO createDefaultBlog(Long ownerUserId, String nickname) {
        BlogVO blog = new BlogVO();
        blog.setOwnerUserId(ownerUserId);
        blog.setBlogName(nickname + "의 블로그");
        blog.setIsPublic(true);
        blog.setBoardLimit(5);
        blog.setImageLimitCount(5);
        blog.setImageSizeLimitBytes(5L * 1024L * 1024L);
        blog.setFileSizeLimitBytes(10485760L);
        blog.setTotalCapacityBytes(314572800L);
        blogMapper.insertBlog(blog);
        insertDefaultBoard(blog.getBlogId());
        return blog;
    }

    private void insertDefaultBoard(Long blogId) {
        BoardVO board = new BoardVO();
        board.setBlogId(blogId);
        board.setBoardName("기본 게시판");
        board.setSortOrder(0);
        boardMapper.insertBoard(board);
    }

    @Override
    public BlogVO getBlogById(Long blogId) {
        return blogMapper.selectBlogById(blogId);
    }

    @Override
    public BlogVO getBlogByOwnerUserId(Long ownerUserId) {
        return blogMapper.selectBlogByOwnerUserId(ownerUserId);
    }

    @Override
    public List<BlogVO> getAllBlogs() {
        return blogMapper.selectAllBlogs();
    }

    @Override
    public void updateBlogSetting(BlogVO blog) {
        blogMapper.updateBlog(blog);
    }

    @Override
    public void addMember(BlogMemberVO member) {
        blogMapper.insertBlogMember(member);
    }

    @Override
    public List<BlogMemberVO> getMembers(Long blogId) {
        return blogMapper.selectBlogMembers(blogId);
    }

    @Override
    public void changeMemberStatus(BlogMemberVO member) {
        blogMapper.updateBlogMemberStatus(member);
    }

    @Override
    public Long resolveLandingBlogId(Long userId) {
        if (userId == null) {
            return null;
        }
        BlogVO owned = blogMapper.selectBlogByOwnerUserId(userId);
        if (owned != null) {
            return owned.getBlogId();
        }
        return blogMapper.selectActiveMemberBlogId(userId);
    }

    @Override
    public Long findActiveMemberBlogId(Long userId) {
        if (userId == null) {
            return null;
        }
        return blogMapper.selectActiveMemberBlogId(userId);
    }

    @Override
    public boolean canWritePost(Long blogId, Long userId) {
        if (blogId == null || userId == null) {
            return false;
        }

        BlogVO blog = blogMapper.selectBlogById(blogId);
        if (blog == null) {
            return false;
        }

        if (userId.equals(blog.getOwnerUserId())) {
            return true;
        }

        Integer count = blogMapper.countActiveMembership(blogId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean isBlogOwner(Long blogId, Long userId) {
        if (blogId == null || userId == null) {
            return false;
        }
        BlogVO blog = blogMapper.selectBlogById(blogId);
        return blog != null && userId.equals(blog.getOwnerUserId());
    }

    @Override
    public List<BlogVO> listPublicBlogs() {
        return blogMapper.selectPublicBlogs();
    }

    @Override
    public void joinBlog(Long blogId, Long userId) {
        BlogVO blog = blogMapper.selectBlogById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("블로그를 찾을 수 없습니다.");
        }
        if (!blog.getIsPublic()) {
            throw new IllegalArgumentException("비공개 블로그에는 멤버로 가입할 수 없습니다.");
        }
        if (userId.equals(blog.getOwnerUserId())) {
            throw new IllegalStateException("블로그 소유자는 멤버 가입이 필요하지 않습니다.");
        }
        BlogMemberVO existing = blogMapper.selectMembership(blogId, userId);
        if (existing != null) {
            if ("ACTIVE".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("이미 멤버로 가입되어 있습니다.");
            }
            BlogMemberVO upd = new BlogMemberVO();
            upd.setMemberId(existing.getMemberId());
            upd.setStatus("ACTIVE");
            blogMapper.updateBlogMemberStatus(upd);
            return;
        }
        BlogMemberVO member = new BlogMemberVO();
        member.setBlogId(blogId);
        member.setUserId(userId);
        member.setMemberRole("MEMBER");
        member.setStatus("ACTIVE");
        blogMapper.insertBlogMember(member);
    }

    @Override
    public BlogVO createOwnedBlog(Long ownerUserId, String blogName, BlogUserVO actingUser) {
        if (actingUser == null || !actingUser.getUserId().equals(ownerUserId)) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        if (!actingUser.isApproved()) {
            throw new IllegalStateException("승인되지 않은 계정입니다.");
        }
        if (!actingUser.isCanCreateBlog()) {
            throw new IllegalStateException("블로그 생성 권한이 없습니다.");
        }
        if (!"BLOG_ADMIN".equalsIgnoreCase(actingUser.getRole())) {
            throw new IllegalStateException("블로그 관리자 역할만 블로그를 개설할 수 있습니다.");
        }
        BlogVO exists = blogMapper.selectBlogByOwnerUserId(ownerUserId);
        if (exists != null) {
            throw new IllegalStateException("이미 블로그가 있습니다.");
        }
        BlogVO blog = new BlogVO();
        blog.setOwnerUserId(ownerUserId);
        blog.setBlogName((blogName != null && !blogName.trim().isEmpty()) ? blogName.trim()
                : actingUser.getNickname() + "의 블로그");
        blog.setIsPublic(true);
        blog.setBoardLimit(5);
        blog.setImageLimitCount(5);
        blog.setImageSizeLimitBytes(5L * 1024L * 1024L);
        blog.setFileSizeLimitBytes(10485760L);
        blog.setTotalCapacityBytes(314572800L);
        blogMapper.insertBlog(blog);
        insertDefaultBoard(blog.getBlogId());
        return blog;
    }

    @Override
    public boolean canReadBlog(Long blogId, Long userIdOrNull) {
        BlogVO blog = blogMapper.selectBlogById(blogId);
        if (blog == null) {
            return false;
        }
        if (blog.getIsPublic()) {
            return true;
        }
        if (userIdOrNull == null) {
            return false;
        }
        if (userIdOrNull.equals(blog.getOwnerUserId())) {
            return true;
        }
        Integer count = blogMapper.countActiveMembership(blogId, userIdOrNull);
        return count != null && count > 0;
    }
}
