package com.chan.service;

import java.util.List;

import com.chan.vo.BlacklistWordVO;
import com.chan.vo.ModerationViolationVO;

public interface ModerationService {
    boolean containsBlockedWord(Long blogId, String text);

    /** 전역 + 블로그 금지어 중 text 에 처음 걸리는 단어. 없으면 null. */
    String findBlockedWord(Long blogId, String text);

    /** 등록 차단 시도를 moderation_violations 에 기록한다. 기록 실패는 무시한다. */
    void recordViolation(ModerationViolationVO violation);

    boolean containsPersonalInfo(String text);

    List<BlacklistWordVO> listGlobalBlacklistWords();

    List<BlacklistWordVO> listBlogBlacklistWords(Long blogId);

    void addGlobalBlacklistWord(String word);

    void addBlogBlacklistWord(Long blogId, String word);

    void deleteGlobalBlacklistWord(Long blacklistId);

    void deleteBlogBlacklistWord(Long blogId, Long blacklistId);
}
