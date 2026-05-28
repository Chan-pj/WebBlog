package com.chan.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.chan.service.BlogService;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;

@Controller
public class HomeController {

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    @Resource
    private BlogService blogService;

    @GetMapping("/")
    public String home(HttpSession session) {
        BlogUserVO user = (BlogUserVO) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (user == null) {
            return "redirect:/auth/login.do";
        }
        if ("SUPER_ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard.do";
        }
        if (!user.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        BlogVO owned = blogService.getBlogByOwnerUserId(user.getUserId());
        if (owned != null) {
            return "redirect:/blog/" + owned.getBlogId() + "/view.do";
        }
        Long memberBlogId = blogService.findActiveMemberBlogId(user.getUserId());
        if (memberBlogId != null) {
            return "redirect:/blog/" + memberBlogId + "/view.do";
        }
        return "redirect:/auth/pendingApproval.do";
    }

    @GetMapping("/index.jsp")
    public String indexJsp(HttpSession session) {
        BlogUserVO user = (BlogUserVO) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (user == null) {
            return "redirect:/auth/login.do";
        }
        return "redirect:/";
    }
}
