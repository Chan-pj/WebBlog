package com.ylove.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ylove.vo.BlacklistWordVO;

@Mapper
public interface ModerationMapper {
    List<BlacklistWordVO> selectGlobalBlacklist();

    List<BlacklistWordVO> selectBlogBlacklist(Long blogId);

    int insertBlacklistWord(BlacklistWordVO vo);

    int deleteGlobalBlacklistWord(@Param("blacklistId") Long blacklistId);

    int deleteBlogBlacklistWord(@Param("blogId") Long blogId, @Param("blacklistId") Long blacklistId);
}
