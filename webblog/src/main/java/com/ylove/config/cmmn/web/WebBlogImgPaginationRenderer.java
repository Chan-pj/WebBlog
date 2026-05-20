package com.ylove.config.cmmn.web;

import javax.servlet.ServletContext;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.AbstractPaginationRenderer;
import org.springframework.web.context.ServletContextAware;

/**
 * eGov 페이지네이션 이미지 렌더러. {@code egovframework.example} 오타·WTP 클래스 누락 대비용.
 */
public class WebBlogImgPaginationRenderer extends AbstractPaginationRenderer implements ServletContextAware {

	private ServletContext servletContext;

	public WebBlogImgPaginationRenderer() {
		// no-op
	}

	public void initVariables() {
		String ctx = servletContext.getContextPath();
		firstPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">" + "<image src='" + ctx
				+ "/images/egovframework/cmmn/btn_page_pre10.gif' border=0/></a>&#160;";
		previousPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">" + "<image src='" + ctx
				+ "/images/egovframework/cmmn/btn_page_pre1.gif' border=0/></a>&#160;";
		currentPageLabel = "<strong>{0}</strong>&#160;";
		otherPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">{2}</a>&#160;";
		nextPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">" + "<image src='" + ctx
				+ "/images/egovframework/cmmn/btn_page_next1.gif' border=0/></a>&#160;";
		lastPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\">" + "<image src='" + ctx
				+ "/images/egovframework/cmmn/btn_page_next10.gif' border=0/></a>&#160;";
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		initVariables();
	}
}
