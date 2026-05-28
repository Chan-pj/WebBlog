package com.chan.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.chan.vo.BlacklistWordVO;
import com.chan.vo.ModerationViolationVO;

@Mapper
public interface ModerationMapper {
    List<BlacklistWordVO> selectGlobalBlacklist();
    List<BlacklistWordVO> selectBlogBlacklist(Long blogId);
    List<BlacklistWordVO> selectAllBlacklistWords();
    int insertBlacklistWord(BlacklistWordVO vo);
    int insertViolation(ModerationViolationVO vo);
    int countViolations();
    int countViolationsByType(String violationType);
    List<ModerationViolationVO> selectViolationsForReport(@Param("limit") int limit);

    void deleteGlobalBlacklistWord(Long blacklistId);
    void deleteBlogBlacklistWord(@Param("blogId") Long blogId, @Param("blacklistId") Long blacklistId);
}