package com.chan.service;
import java.util.List;
import com.chan.vo.BlogUserVO;
public interface UserService {
    /**
     * 카카오 계정으로 로그인한 사용자를 찾거나 새로 만든다.
     * emailVerified 가 true 이고 같은 이메일의 미연동 계정이 있으면 그 계정에 카카오 ID 를 연결한다.
     */
    BlogUserVO registerKakaoUser(String kakaoId, String email, boolean emailVerified, String nickname);
    BlogUserVO getUserById(Long userId);
    List<BlogUserVO> getAllUsers();
    List<BlogUserVO> getUsersWithoutOwnedBlog();
    long countAllUsers();
    void updateApprovalAndPermission(BlogUserVO user);
    void updateProtection(Long userId, boolean managementBlocked, boolean profileRestricted);
    void deleteUser(Long userId); // ← 추가
}