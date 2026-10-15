package com.chan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chan.mapper.ModerationMapper;
import com.chan.service.ModerationService;
import com.chan.vo.BlacklistWordVO;
import com.chan.vo.ModerationViolationVO;

@Service("moderationService")
public class ModerationServiceImpl implements ModerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationServiceImpl.class);

    @Resource
    private ModerationMapper moderationMapper;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("01[016789]-?\\d{3,4}-?\\d{4}");

    private static final Pattern ACCOUNT_PATTERN =
            Pattern.compile("\\d{2,4}-\\d{2,6}-\\d{2,6}");

    @Override
    public boolean containsBlockedWord(Long blogId, String text) {
        return findBlockedWord(blogId, text) != null;
    }

    @Override
    public String findBlockedWord(Long blogId, String text) {
        if (text == null) {
            return null;
        }
        List<BlacklistWordVO> totalList = new ArrayList<>();
        totalList.addAll(moderationMapper.selectGlobalBlacklist());
        if (blogId != null) {
            totalList.addAll(moderationMapper.selectBlogBlacklist(blogId));
        }

        for (BlacklistWordVO vo : totalList) {
            String word = vo.getWord();
            if (word != null && !word.isEmpty() && text.contains(word)) {
                return word;
            }
        }
        return null;
    }

    @Override
    public void recordViolation(ModerationViolationVO violation) {
        try {
            moderationMapper.insertViolation(violation);
        } catch (Exception e) {
            LOGGER.warn("moderation violation record failed: type={}", violation.getViolationType(), e);
        }
    }

    @Override
    public boolean containsPersonalInfo(String text) {
        if (text == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(text).find()
                || PHONE_PATTERN.matcher(text).find()
                || ACCOUNT_PATTERN.matcher(text).find();
    }

    @Override
    public List<BlacklistWordVO> listGlobalBlacklistWords() {
        return moderationMapper.selectGlobalBlacklist();
    }

    @Override
    public List<BlacklistWordVO> listBlogBlacklistWords(Long blogId) {
        if (blogId == null) {
            return new ArrayList<>();
        }
        return moderationMapper.selectBlogBlacklist(blogId);
    }

    @Override
    public void addGlobalBlacklistWord(String word) {
        String w = normalizeWord(word);
        BlacklistWordVO vo = new BlacklistWordVO();
        vo.setBlogId(null);
        vo.setWord(w);
        moderationMapper.insertBlacklistWord(vo);
    }

    @Override
    public void addBlogBlacklistWord(Long blogId, String word) {
        String w = normalizeWord(word);
        BlacklistWordVO vo = new BlacklistWordVO();
        vo.setBlogId(blogId);
        vo.setWord(w);
        moderationMapper.insertBlacklistWord(vo);
    }

    @Override
    public void deleteGlobalBlacklistWord(Long blacklistId) {
        if (blacklistId == null) {
            return;
        }
        moderationMapper.deleteGlobalBlacklistWord(blacklistId);
    }

    @Override
    public void deleteBlogBlacklistWord(Long blogId, Long blacklistId) {
        if (blogId == null || blacklistId == null) {
            return;
        }
        moderationMapper.deleteBlogBlacklistWord(blogId, blacklistId);
    }

    private static String normalizeWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("금지어를 입력해 주세요.");
        }
        return word.trim();
    }
}
