package com.ylove.service;

import java.util.List;

import com.ylove.vo.AdminSearchResultVO;

public interface AdminSearchService {

    String TYPE_ALL = "ALL";
    String TYPE_POST = "POST";
    String TYPE_USER = "USER";
    String TYPE_BLOG = "BLOG";
    String TYPE_FORBIDDEN = "FORBIDDEN";
    String TYPE_VIOLATION = "VIOLATION";

    List<AdminSearchResultVO> search(String searchType, String keyword, int limit);
}