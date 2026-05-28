package com.chan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chan.mapper.AdminSearchMapper;
import com.chan.mapper.ModerationMapper;
import com.chan.service.AdminSearchService;
import com.chan.util.SearchTextUtil;
import com.chan.vo.AdminSearchResultVO;
import com.chan.vo.BlacklistWordVO;

@Service("adminSearchService")
public class AdminSearchServiceImpl implements AdminSearchService {

    private static final int DEFAULT_LIMIT = 50;

    @Resource
    private AdminSearchMapper adminSearchMapper;

    @Resource
    private ModerationMapper moderationMapper;

    @Override
    public List<AdminSearchResultVO> search(String searchType, String keyword, int limit) {
        int max = limit > 0 ? Math.min(limit, 200) : DEFAULT_LIMIT;
        String type = searchType == null ? TYPE_ALL : searchType.trim().toUpperCase();
        String kw = keyword == null ? "" : keyword.trim();

        List<AdminSearchResultVO> results = new ArrayList<>();
        if (TYPE_ALL.equals(type)) {
            if (!kw.isEmpty()) {
                results.addAll(search(TYPE_POST, kw, max));
                results.addAll(search(TYPE_USER, kw, max));
                results.addAll(search(TYPE_BLOG, kw, max));
                results.addAll(search(TYPE_VIOLATION, kw, max));
            }
            results.addAll(searchForbidden(kw, max));
            return dedupeAndEnrich(results, max * 2);
        }
        if (TYPE_FORBIDDEN.equals(type)) {
            return dedupeAndEnrich(searchForbidden(kw, max), max);
        }
        if (kw.isEmpty() && !TYPE_VIOLATION.equals(type)) {
            return results;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("limit", max);
        String likeKeyword = SearchTextUtil.toLikeKeyword(kw);
        params.put("likeKeyword", likeKeyword);

        if (TYPE_POST.equals(type)) {
            results = adminSearchMapper.searchPosts(params);
        } else if (TYPE_USER.equals(type)) {
            results = adminSearchMapper.searchUsers(params);
        } else if (TYPE_BLOG.equals(type)) {
            results = adminSearchMapper.searchBlogs(params);
        } else if (TYPE_VIOLATION.equals(type)) {
            results = adminSearchMapper.searchViolations(params);
        }
        return dedupeAndEnrich(results, max);
    }

    private List<AdminSearchResultVO> searchForbidden(String keyword, int max) {
        List<BlacklistWordVO> words = moderationMapper.selectAllBlacklistWords();
        List<AdminSearchResultVO> found = new ArrayList<>();
        Map<Long, AdminSearchResultVO> byPost = new LinkedHashMap<>();

        for (BlacklistWordVO bw : words) {
            String word = bw.getWord();
            if (word == null || word.trim().isEmpty()) {
                continue;
            }
            if (keyword != null && !keyword.isEmpty() && !word.contains(keyword)) {
                continue;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("word", word);
            params.put("likeWord", SearchTextUtil.toLikeKeyword(word));
            params.put("limit", max);
            for (AdminSearchResultVO row : adminSearchMapper.searchPostsByForbiddenWord(params)) {
                if (row.getPostId() != null) {
                    byPost.put(row.getPostId(), row);
                }
            }
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("word", keyword.trim());
            params.put("likeWord", SearchTextUtil.toLikeKeyword(keyword.trim()));
            params.put("limit", max);
            for (AdminSearchResultVO row : adminSearchMapper.searchPostsByForbiddenWord(params)) {
                if (row.getPostId() != null) {
                    row.setMatchedWord(keyword.trim());
                    byPost.put(row.getPostId(), row);
                }
            }
        }

        found.addAll(byPost.values());
        return found;
    }

    private List<AdminSearchResultVO> dedupeAndEnrich(List<AdminSearchResultVO> rows, int max) {
        Map<String, AdminSearchResultVO> unique = new LinkedHashMap<>();
        for (AdminSearchResultVO row : rows) {
            String key = row.getResultType() + "-"
                    + (row.getPostId() != null ? row.getPostId() : "")
                    + (row.getUserId() != null ? row.getUserId() : "")
                    + (row.getBlogId() != null ? row.getBlogId() : "")
                    + (row.getCreatedAt() != null ? row.getCreatedAt() : "");
            unique.putIfAbsent(key, row);
            if (unique.size() >= max) {
                break;
            }
        }
        List<AdminSearchResultVO> list = new ArrayList<>(unique.values());
        for (AdminSearchResultVO row : list) {
            enrichDetailUrl(row);
            if (row.getSnippet() != null && row.getMatchedWord() != null) {
                row.setSnippet(SearchTextUtil.buildSnippet(row.getSnippet(), row.getMatchedWord(), 150));
            }
        }
        return list;
    }

    private void enrichDetailUrl(AdminSearchResultVO row) {
        if (row == null) {
            return;
        }
        if (row.getPostId() != null) {
            row.setDetailUrl("/post/view/" + row.getPostId() + ".do");
        } else if (row.getBlogId() != null) {
            row.setDetailUrl("/blog/" + row.getBlogId() + "/view.do");
        } else if (row.getUserId() != null) {
            row.setDetailUrl("/admin/search.do?searchType=USER&keyword=" + row.getUserEmail());
        }
    }
}