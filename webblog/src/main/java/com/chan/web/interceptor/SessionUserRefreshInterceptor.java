package com.chan.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.chan.service.UserService;
import com.chan.vo.BlogUserVO;

/**
 * 세션의 loginUser 를 매 요청마다 DB 값으로 다시 읽어 온다.
 * 관리자가 승인·권한·역할을 바꾸면 재로그인 없이 바로 반영되고,
 * 삭제된 회원은 세션에서 로그아웃 처리된다.
 * AdminAuthInterceptor 보다 먼저 실행되어야 한다.
 */
public class SessionUserRefreshInterceptor implements HandlerInterceptor {

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    private final UserService userService;

    public SessionUserRefreshInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return true;
        }
        Object sessionUser = session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (!(sessionUser instanceof BlogUserVO)) {
            return true;
        }
        Long userId = ((BlogUserVO) sessionUser).getUserId();
        BlogUserVO fresh = userId != null ? userService.getUserById(userId) : null;
        if (fresh == null) {
            session.removeAttribute(LOGIN_USER_SESSION_KEY);
        } else {
            session.setAttribute(LOGIN_USER_SESSION_KEY, fresh);
        }
        return true;
    }
}
