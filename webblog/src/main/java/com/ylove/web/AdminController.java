package com.ylove.web;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ylove.service.BlogService;
import com.ylove.service.ModerationService;
import com.ylove.service.UserService;
import com.ylove.vo.BlogUserVO;
import com.ylove.vo.BlogVO;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private ModerationService moderationService;

    @GetMapping("/dashboard.do")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "admin/dashboard";
    }

    @GetMapping("/blogs.do")
    public String blogList(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "admin/blogList";
    }

    @PostMapping("/blog/update.do")
    public String updateBlog(@ModelAttribute BlogVO blog) {
        blogService.updateBlogSetting(blog);
        return "redirect:/admin/blogs.do";
    }

    @PostMapping("/user/approve.do")
    public String approveUser(@RequestParam("userId") Long userId,
                              @RequestParam(value = "approved", defaultValue = "false") boolean approved,
                              @RequestParam(value = "canCreateBlog", defaultValue = "false") boolean canCreateBlog,
                              @RequestParam(value = "canCreateBoard", defaultValue = "false") boolean canCreateBoard,
                              @RequestParam(value = "boardCreateQuota", defaultValue = "0") int boardCreateQuota,
                              @RequestParam(value = "imageUploadLimitCount", defaultValue = "5") int imageUploadLimitCount,
                              @RequestParam(value = "imageSizeLimitBytes", defaultValue = "5242880")
                              long imageSizeLimitBytes) {
        BlogUserVO user = new BlogUserVO();
        user.setUserId(userId);
        user.setApproved(approved);
        user.setCanCreateBlog(canCreateBlog);
        user.setCanCreateBoard(canCreateBoard);
        user.setBoardCreateQuota(Math.max(0, boardCreateQuota));
        user.setImageUploadLimitCount(Math.max(0, imageUploadLimitCount));
        user.setImageSizeLimitBytes(Math.max(0L, imageSizeLimitBytes));
        userService.updateApprovalAndPermission(user);
        return "redirect:/admin/dashboard.do";
    }

    @PostMapping("/user/protection.do")
    public String updateProtection(@RequestParam("userId") Long userId,
                                   @RequestParam(value = "managementBlocked", defaultValue = "false")
                                   boolean managementBlocked,
                                   @RequestParam(value = "profileRestricted", defaultValue = "false")
                                   boolean profileRestricted) {
        userService.updateProtection(userId, managementBlocked, profileRestricted);
        return "redirect:/admin/dashboard.do";
    }

    @GetMapping("/blacklist.do")
    public String globalBlacklist(Model model) {
        model.addAttribute("words", moderationService.listGlobalBlacklistWords());
        return "admin/blacklist";
    }

    @PostMapping("/blacklist/add.do")
    public String globalBlacklistAdd(@RequestParam("word") String word) {
        try {
            moderationService.addGlobalBlacklistWord(word);
        } catch (RuntimeException ex) {
            return "redirect:/admin/blacklist.do?error=1";
        }
        return "redirect:/admin/blacklist.do";
    }

    @PostMapping("/blacklist/delete.do")
    public String globalBlacklistDelete(@RequestParam("blacklistId") Long blacklistId) {
        moderationService.deleteGlobalBlacklistWord(blacklistId);
        return "redirect:/admin/blacklist.do";
    }
}