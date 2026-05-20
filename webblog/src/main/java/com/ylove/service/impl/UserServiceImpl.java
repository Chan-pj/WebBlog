package com.ylove.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylove.mapper.UserMapper;
import com.ylove.service.UserService;
import com.ylove.vo.BlogUserVO;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public BlogUserVO registerUser(BlogUserVO user) {
        BlogUserVO exists = userMapper.selectUserByEmail(user.getEmail());
        if (exists != null) {
            return exists;
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("BLOG_MEMBER");
        }
        user.setApproved(false);
        user.setCanCreateBlog(false);
        user.setCanCreateBoard(false);
        user.setBoardCreateQuota(0);
        user.setImageUploadLimitCount(5);
        user.setImageSizeLimitBytes(5L * 1024L * 1024L);
        user.setManagementBlocked(false);
        user.setProfileRestricted(false);
        userMapper.insertUser(user);
        return user;
    }

    @Override
    @Transactional
    public BlogUserVO registerKakaoUser(String kakaoId, String email, String nickname) {
        BlogUserVO exists = userMapper.selectUserByKakaoId(kakaoId);
        if (exists != null) {
            return exists;
        }

        BlogUserVO user = new BlogUserVO();
        user.setKakaoId(kakaoId);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole("BLOG_ADMIN");
        user.setApproved(false);
        user.setCanCreateBlog(false);
        user.setCanCreateBoard(false);
        user.setBoardCreateQuota(0);
        user.setImageUploadLimitCount(5);
        user.setImageSizeLimitBytes(5L * 1024L * 1024L);
        user.setManagementBlocked(false);
        user.setProfileRestricted(false);
        user.setUseKakaoAuth(true);

        userMapper.insertUser(user);
        return user;
    }

    @Override
    public BlogUserVO getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public BlogUserVO getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<BlogUserVO> getAllUsers() {
        return userMapper.selectAllUsers();
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
}