package com.chan.web.interceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 세션당 CSRF 토큰을 발급하고, 상태를 바꾸는 요청(POST 등)에서 토큰을 검증한다.
 * JSP 폼에서는 {@code <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"/>} 로 전송한다.
 */
public class CsrfTokenInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfTokenInterceptor.class);

    public static final String SESSION_KEY = "csrfToken";
    public static final String PARAM_NAME = "_csrf";
    public static final String HEADER_NAME = "X-CSRF-Token";

    private final SecureRandom random = new SecureRandom();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String expected = (String) session.getAttribute(SESSION_KEY);
        if (expected == null) {
            expected = newToken();
            session.setAttribute(SESSION_KEY, expected);
        }

        if (!isStateChanging(request.getMethod())) {
            return true;
        }

        String actual = request.getHeader(HEADER_NAME);
        if (actual == null) {
            actual = request.getParameter(PARAM_NAME);
        }
        if (actual != null && constantTimeEquals(expected, actual)) {
            return true;
        }

        LOGGER.warn("CSRF token mismatch: method={}, uri={}", request.getMethod(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<!doctype html><meta charset=\"UTF-8\"><title>요청 거부</title>"
                + "<p>요청이 만료되었거나 올바르지 않습니다. 페이지를 새로고침한 뒤 다시 시도해 주세요.</p>"
                + "<p><a href=\"" + request.getContextPath() + "/\">홈으로</a></p>");
        return false;
    }

    private static boolean isStateChanging(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);
    }

    private String newToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
