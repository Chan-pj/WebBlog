package com.chan.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chan.mapper.PostMapper;
import com.chan.mapper.UserMapper;
import com.chan.service.BlogService;
import com.chan.service.UserService;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private BlogService blogService;

    @Override
    @Transactional
    public BlogUserVO registerKakaoUser(String kakaoId, String email, boolean emailVerified, String nickname) {
        BlogUserVO exists = userMapper.selectUserByKakaoId(kakaoId);
        if (exists != null) return exists;

        String fallbackEmail = "kakao_" + kakaoId;
        if (email != null) {
            BlogUserVO sameEmail = userMapper.selectUserByEmail(email);
            if (sameEmail != null) {
                // 카카오가 소유를 인증한 이메일일 때만 기존(이메일 가입) 계정에 연결한다.
                if (emailVerified && sameEmail.getKakaoId() == null) {
                    userMapper.linkKakaoId(sameEmail.getUserId(), kakaoId);
                    return userMapper.selectUserByKakaoId(kakaoId);
                }
                email = null;
            }
        }

        BlogUserVO user = new BlogUserVO();
        user.setKakaoId(kakaoId);
        user.setEmail(email != null ? email : fallbackEmail);
        user.setNickname(nickname);
        user.setRole("BLOG_MEMBER");
        user.setUseKakaoAuth(true);
        userMapper.insertUser(user);
        return userMapper.selectUserByKakaoId(kakaoId);
    }

    @Override
    public BlogUserVO getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<BlogUserVO> getAllUsers(int offset, int limit) {
        return userMapper.selectAllUsers(offset, limit);
    }

    @Override
    public int countUsersExceptSuperAdmin() {
        return userMapper.countUsersExceptSuperAdmin();
    }

    @Override
    public List<BlogUserVO> getUsersWithoutOwnedBlog() {
        return userMapper.selectUsersWithoutOwnedBlog();
    }

    @Override
    public long countAllUsers() {
        return userMapper.countAllUsers();
    }

    @Override
    @Transactional
    public void updateApprovalAndPermission(BlogUserVO user) {
        userMapper.updateApprovalAndPermission(user);
    }

    @Override
    @Transactional
    public void updateProtection(Long userId, boolean managementBlocked, boolean profileRestricted) {
        userMapper.updateProtection(userId, managementBlocked, profileRestricted);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // blog.owner_user_id, post.writer_user_id 가 ON DELETE RESTRICT 라 먼저 정리한다.
        // 소유 블로그(그 안의 모든 글·게시판) → 다른 블로그에 쓴 글 → 회원 순. 멤버십은 CASCADE.
        BlogVO owned = blogService.getBlogByOwnerUserId(userId);
        if (owned != null) {
            blogService.deleteBlog(owned.getBlogId());
        }
        postMapper.deletePostsByWriterUserId(userId);
        userMapper.deleteUser(userId);
    }
}