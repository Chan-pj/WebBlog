package com.chan.config.cmmn;

import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebBlogOthersExcepHndlr implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebBlogOthersExcepHndlr.class);

    @Override
    public void occur(Exception ex, String packageName) {
        log.error("[Other Exception] packageName : {}", packageName);
        log.error("[Other Exception] message : {}", ex.getMessage());
    }
}