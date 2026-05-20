package com.ylove.service;

import java.util.List;

import com.ylove.vo.BlogUserVO;

public interface UserService {
    BlogUserVO registerUser(BlogUserVO user);
    BlogUserVO registerKakaoUser(String kakaoId, String email, String nickname);
    BlogUserVO getUserByEmail(String email);
    BlogUserVO getUserById(Long userId);
    List<BlogUserVO> getAllUsers();
    void updateApprovalAndPermission(BlogUserVO user);
    void updateProtection(Long userId, boolean managementBlocked, boolean profileRestricted);
}