package com.chan.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.chan.vo.BlogUserVO;

public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login.do?error=login_required");
            return false;
        }

        Object sessionUser = session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (!(sessionUser instanceof BlogUserVO)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.do?error=login_required");
            return false;
        }

        BlogUserVO user = (BlogUserVO) sessionUser;
        if (!"SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/auth/login.do?error=admin_only");
            return false;
        }

        return true;
    }
}
