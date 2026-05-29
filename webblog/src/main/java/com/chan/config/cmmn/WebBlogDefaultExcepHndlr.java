package com.chan.config.cmmn;

import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebBlogDefaultExcepHndlr implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebBlogDefaultExcepHndlr.class);

    @Override
    public void occur(Exception ex, String packageName) {
        log.error("[Default Exception] packageName : {}", packageName);
        log.error("[Default Exception] message : {}", ex.getMessage());
    }
}