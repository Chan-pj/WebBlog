package com.ylove.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ylove.vo.BlogUserVO;

@Mapper
public interface UserMapper {
    int insertUser(BlogUserVO user);
    BlogUserVO selectUserByEmail(String email);
    BlogUserVO selectUserByKakaoId(String kakaoId);
    BlogUserVO selectUserById(Long userId);
    List<BlogUserVO> selectAllUsers();
    int updateApprovalAndPermission(BlogUserVO user);
    int updateProtection(@Param("userId") Long userId,
                         @Param("managementBlocked") boolean managementBlocked,
                         @Param("profileRestricted") boolean profileRestricted);
}