package com.chan.util;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

/**
 * eGov {@link PaginationInfo} 생성 도우미.
 * 요청한 페이지 번호를 1 ~ 마지막 페이지 범위로 맞춘 뒤,
 * {@code getFirstRecordIndex()} / {@code getRecordCountPerPage()} 를 SQL OFFSET / LIMIT 로 쓴다.
 */
public final class PagingUtil {

    /** 목록 한 페이지에 보여줄 개수 */
    public static final int DEFAULT_PER_PAGE = 10;
    /** 페이지 번호 링크를 한 번에 몇 개 보여줄지 */
    public static final int PAGE_LINK_SIZE = 10;

    private PagingUtil() {
    }

    public static PaginationInfo paginate(int pageIndex, int totalCount) {
        return paginate(pageIndex, totalCount, DEFAULT_PER_PAGE);
    }

    public static PaginationInfo paginate(int pageIndex, int totalCount, int perPage) {
        int lastPage = Math.max(1, (totalCount + perPage - 1) / perPage);
        PaginationInfo info = new PaginationInfo();
        info.setRecordCountPerPage(perPage);
        info.setPageSize(PAGE_LINK_SIZE);
        info.setTotalRecordCount(totalCount);
        info.setCurrentPageNo(Math.min(Math.max(pageIndex, 1), lastPage));
        return info;
    }
}
