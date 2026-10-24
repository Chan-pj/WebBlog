package com.chan.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.chan.vo.BlogUserVO;

@Mapper
public interface UserMapper {
    int insertUser(BlogUserVO user);
    BlogUserVO selectUserByEmail(String email);
    BlogUserVO selectUserByKakaoId(String kakaoId);
    BlogUserVO selectUserById(Long userId);
    int linkKakaoId(@Param("userId") Long userId, @Param("kakaoId") String kakaoId);
    List<BlogUserVO> selectAllUsers(@Param("offset") int offset, @Param("limit") int limit);
    int countUsersExceptSuperAdmin();
    List<BlogUserVO> selectUsersWithoutOwnedBlog();
    long countAllUsers();
    void updateApprovalAndPermission(BlogUserVO user);
    void updateProtection(@Param("userId") Long userId,
                          @Param("managementBlocked") boolean managementBlocked,
                          @Param("profileRestricted") boolean profileRestricted);
    void deleteUser(Long userId);
}