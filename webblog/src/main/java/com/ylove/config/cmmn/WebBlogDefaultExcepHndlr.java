package com.ylove.config.cmmn;

import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * eGov {@code ExceptionTransfer} 기본 핸들러. WTP 배포 시 {@code egovframework.config} 출력 누락을 피하기 위해 {@code com.ylove}에 둠.
 */
public class WebBlogDefaultExcepHndlr implements ExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebBlogDefaultExcepHndlr.class);

	@Override
	public void occur(Exception ex, String packageName) {
		LOGGER.debug("WebBlogDefaultExcepHndlr: ex={}, package={}", ex != null ? ex.getMessage() : null, packageName);
	}
}
