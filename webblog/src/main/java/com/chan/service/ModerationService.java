package com.chan.service;

import java.util.List;

import com.chan.vo.BlacklistWordVO;

public interface ModerationService {
    boolean containsBlockedWord(Long blogId, String text);

    boolean containsPersonalInfo(String text);

    List<BlacklistWordVO> listGlobalBlacklistWords();

    List<BlacklistWordVO> listBlogBlacklistWords(Long blogId);

    void addGlobalBlacklistWord(String word);

    void addBlogBlacklistWord(Long blogId, String word);

    void deleteGlobalBlacklistWord(Long blacklistId);

    void deleteBlogBlacklistWord(Long blogId, Long blacklistId);
}
