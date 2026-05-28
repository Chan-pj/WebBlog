package com.chan.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chan.vo.AdminSearchResultVO;

@Mapper
public interface AdminSearchMapper {
    List<AdminSearchResultVO> searchPosts(Map<String, Object> params);

    List<AdminSearchResultVO> searchUsers(Map<String, Object> params);

    List<AdminSearchResultVO> searchBlogs(Map<String, Object> params);

    List<AdminSearchResultVO> searchPostsByForbiddenWord(Map<String, Object> params);

    List<AdminSearchResultVO> searchViolations(Map<String, Object> params);
}