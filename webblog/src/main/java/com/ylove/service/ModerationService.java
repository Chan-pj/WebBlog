package com.ylove.service;

import java.util.List;

import com.ylove.vo.BlacklistWordVO;

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
