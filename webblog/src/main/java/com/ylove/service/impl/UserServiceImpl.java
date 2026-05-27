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
        if (exists != null) return exists;
        userMapper.insertUser(user);
        return user;
    }

    @Override
    @Transactional
    public BlogUserVO registerKakaoUser(String kakaoId, String email, String nickname) {
        BlogUserVO exists = userMapper.selectUserByKakaoId(kakaoId);
        if (exists != null) return exists;
        BlogUserVO user = new BlogUserVO();
        user.setKakaoId(kakaoId);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole("USER");
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
}