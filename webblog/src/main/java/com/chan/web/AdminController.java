package com.chan.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chan.service.AdminSearchService;
import com.chan.service.BlogService;
import com.chan.service.ModerationService;
import com.chan.service.ReportService;
import com.chan.service.UserService;
import com.chan.util.PagingUtil;
import com.chan.vo.AdminSearchResultVO;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /** 관리자가 입력하는 개수(게시판·이미지 등) 상한 */
    private static final int MAX_COUNT = 100;
    /** 이미지 1개 크기 상한: 100MB */
    private static final long MAX_IMAGE_BYTES = 100L * 1024L * 1024L;

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @Resource
    private AdminSearchService adminSearchService;

    @Resource
    private ReportService reportService;

    @Resource
    private ModerationService moderationService;

    @GetMapping("/dashboard.do")
    public String dashboard(@RequestParam(value = "blogPage", defaultValue = "1") int blogPage,
                            @RequestParam(value = "userPage", defaultValue = "1") int userPage,
                            Model model) {
        PaginationInfo blogPaging = PagingUtil.paginate(blogPage, blogService.countAllBlogs());
        PaginationInfo userPaging = PagingUtil.paginate(userPage, userService.countUsersExceptSuperAdmin());
        model.addAttribute("blogs", blogService.getAllBlogs(
                blogPaging.getFirstRecordIndex(), blogPaging.getRecordCountPerPage()));
        model.addAttribute("users", userService.getAllUsers(
                userPaging.getFirstRecordIndex(), userPaging.getRecordCountPerPage()));
        model.addAttribute("blogPaginationInfo", blogPaging);
        model.addAttribute("userPaginationInfo", userPaging);
        model.addAttribute("usersPendingBlogProvision", userService.getUsersWithoutOwnedBlog());
        return "admin/dashboard";
    }

    @GetMapping("/search.do")
    public String search(@RequestParam(value = "searchType", required = false, defaultValue = "ALL") String searchType,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         Model model) {
        List<AdminSearchResultVO> results = adminSearchService.search(searchType, keyword, 100);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("results", results);
        model.addAttribute("resultCount", results.size());
        return "admin/search";
    }

    @GetMapping("/report.do")
    public String reportPage(Model model) {
        model.addAttribute("reportPreview", reportService.buildReportData());
        return "admin/report";
    }

    @GetMapping("/report/word.do")
    public void downloadWordReport(HttpServletResponse response) throws Exception {
        byte[] bytes = reportService.generateWordReport();
        String filename = "webblog-report-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) + ".docx";
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFilename(filename) + "\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/report/pdf.do")
    public void downloadPdfReport(HttpServletResponse response) throws Exception {
        byte[] bytes = reportService.generatePdfReport();
        String filename = "webblog-report-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFilename(filename) + "\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/blogs.do")
    public String blogList(@RequestParam(value = "blogPage", defaultValue = "1") int blogPage, Model model) {
        PaginationInfo blogPaging = PagingUtil.paginate(blogPage, blogService.countAllBlogs());
        model.addAttribute("blogs", blogService.getAllBlogs(
                blogPaging.getFirstRecordIndex(), blogPaging.getRecordCountPerPage()));
        model.addAttribute("blogPaginationInfo", blogPaging);
        return "admin/blogList";
    }

    @PostMapping("/blog/update.do")
    public String updateBlog(@RequestParam("blogId") Long blogId,
                             @RequestParam(value = "blogName", required = false) String blogName,
                             @RequestParam(value = "isPublic", required = false) boolean isPublic,
                             @RequestParam(value = "boardLimit", defaultValue = "5") int boardLimit,
                             @RequestParam(value = "imageLimitCount", defaultValue = "5") int imageLimitCount,
                             @RequestParam(value = "imageSizeLimitBytes", defaultValue = "5242880") long imageSizeLimitBytes) {
        // 폼 값을 VO 에 통째로 바인딩하지 않고, 수정 가능한 항목만 골라 범위를 제한한다.
        BlogVO blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return "redirect:/admin/blogs.do";
        }
        if (blogName != null && !blogName.trim().isEmpty()) {
            blog.setBlogName(blogName.trim());
        }
        blog.setIsPublic(isPublic);
        blog.setBoardLimit(clamp(boardLimit, 1, MAX_COUNT));
        blog.setImageLimitCount(clamp(imageLimitCount, 0, MAX_COUNT));
        blog.setImageSizeLimitBytes(clamp(imageSizeLimitBytes, 0L, MAX_IMAGE_BYTES));
        blogService.updateBlogSetting(blog);
        return "redirect:/admin/blogs.do";
    }

    @PostMapping("/user/provisionBlog.do")
    public String provisionBlog(@RequestParam("userId") Long userId) {
        BlogUserVO target = userService.getUserById(userId);
        if (target == null || "SUPER_ADMIN".equalsIgnoreCase(target.getRole())) {
            return "redirect:/admin/dashboard.do";
        }
        if (blogService.getBlogByOwnerUserId(userId) != null) {
            return "redirect:/admin/dashboard.do";
        }
        blogService.createDefaultBlog(userId, target.getNickname());
        return "redirect:/admin/dashboard.do";
    }

    @PostMapping("/user/approve.do")
    public String approveUser(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "approved", required = false) boolean approved,
            @RequestParam(value = "canCreateBlog", required = false) boolean canCreateBlog,
            @RequestParam(value = "canCreateBoard", required = false) boolean canCreateBoard,
            @RequestParam(value = "boardCreateQuota", defaultValue = "0") int boardCreateQuota,
            @RequestParam(value = "imageUploadLimitCount", defaultValue = "5") int imageUploadLimitCount,
            @RequestParam(value = "imageSizeLimitBytes", defaultValue = "5242880") long imageSizeLimitBytes) {
        BlogUserVO vo = new BlogUserVO();
        vo.setUserId(userId);
        vo.setApproved(approved);
        vo.setCanCreateBlog(canCreateBlog);
        vo.setCanCreateBoard(canCreateBoard);
        vo.setBoardCreateQuota(clamp(boardCreateQuota, 0, MAX_COUNT));
        vo.setImageUploadLimitCount(clamp(imageUploadLimitCount, 0, MAX_COUNT));
        vo.setImageSizeLimitBytes(clamp(imageSizeLimitBytes, 0L, MAX_IMAGE_BYTES));
        userService.updateApprovalAndPermission(vo);
        return "redirect:/admin/dashboard.do";
    }

    @PostMapping("/user/protection.do")
    public String updateProtection(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "managementBlocked", required = false) boolean managementBlocked,
            @RequestParam(value = "profileRestricted", required = false) boolean profileRestricted) {
        userService.updateProtection(userId, managementBlocked, profileRestricted);
        return "redirect:/admin/dashboard.do";
    }

    @GetMapping("/blacklist.do")
    public String blacklistPage(Model model) {
        model.addAttribute("globalBlacklist", moderationService.listGlobalBlacklistWords());
        return "admin/blacklist";
    }

    @PostMapping("/blacklist/add.do")
    public String addBlacklistWord(@RequestParam("word") String word) {
        moderationService.addGlobalBlacklistWord(word);
        return "redirect:/admin/blacklist.do";
    }

    @PostMapping("/blacklist/delete.do")
    public String deleteBlacklistWord(@RequestParam("blacklistId") Long blacklistId) {
        moderationService.deleteGlobalBlacklistWord(blacklistId);
        return "redirect:/admin/blacklist.do";
    }
    
    @PostMapping("/user/delete.do")
    public String deleteUser(@RequestParam("userId") Long userId, HttpSession session) {
        Object me = session.getAttribute("loginUser");
        if (me instanceof BlogUserVO && userId.equals(((BlogUserVO) me).getUserId())) {
            return "redirect:/admin/dashboard.do";
        }
        userService.deleteUser(userId);
        return "redirect:/admin/dashboard.do";
    }

    @PostMapping("/blog/delete.do")
    public String deleteBlog(@RequestParam("blogId") Long blogId) {
        blogService.deleteBlog(blogId);
        return "redirect:/admin/blogs.do";
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String encodeFilename(String filename) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (Exception e) {
            return filename;
        }
    }
}