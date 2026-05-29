package com.chan.service;
import java.util.List;
import com.chan.vo.BlogUserVO;
public interface UserService {
    BlogUserVO registerUser(BlogUserVO user);
    BlogUserVO registerKakaoUser(String kakaoId, String email, String nickname);
    BlogUserVO getUserByEmail(String email);
    BlogUserVO getUserById(Long userId);
    List<BlogUserVO> getAllUsers();
    List<BlogUserVO> getUsersWithoutOwnedBlog();
    long countAllUsers();
    void updateApprovalAndPermission(BlogUserVO user);
    void updateProtection(Long userId, boolean managementBlocked, boolean profileRestricted);
    void deleteUser(Long userId); // ← 추가
}