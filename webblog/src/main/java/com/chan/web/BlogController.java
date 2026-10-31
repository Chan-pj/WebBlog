package com.chan.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chan.service.BlogService;
import com.chan.service.BoardService;
import com.chan.service.ModerationService;
import com.chan.util.PagingUtil;
import com.chan.vo.BlogMemberVO;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    @Resource
    private BlogService blogService;

    @Resource
    private BoardService boardService;

    @Resource
    private ModerationService moderationService;

    @GetMapping("/explore.do")
    public String explore(@RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex, Model model) {
        PaginationInfo paging = PagingUtil.paginate(pageIndex, blogService.countPublicBlogs());
        model.addAttribute("blogs", blogService.listPublicBlogs(
                paging.getFirstRecordIndex(), paging.getRecordCountPerPage()));
        model.addAttribute("paginationInfo", paging);
        return "blog/blogExplore";
    }

    @GetMapping("/setup/create.do")
    public String createBlogForm(HttpSession session, Model model) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!u.isCanCreateBlog()) {
            model.addAttribute("message", "블로그 개설 권한이 없습니다.");
            return "blog/blogSetupCreate";
        }
        if (blogService.getBlogByOwnerUserId(u.getUserId()) != null) {
            return "redirect:/";
        }
        return "blog/blogSetupCreate";
    }

    @PostMapping("/setup/create.do")
    public String createBlogSubmit(@RequestParam(value = "blogName", required = false) String blogName,
                                   HttpSession session,
                                   Model model) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        try {
            BlogVO blog = blogService.createOwnedBlog(u.getUserId(), blogName, u);
            return "redirect:/blog/" + blog.getBlogId() + "/view.do";
        } catch (RuntimeException ex) {
            model.addAttribute("message", ex.getMessage());
            return "blog/blogSetupCreate";
        }
    }

    @GetMapping("/{blogId}/view.do")
    public String viewBlog(@PathVariable("blogId") Long blogId, Model model, HttpSession session) {
        BlogVO blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return "redirect:/blog/explore.do";
        }
        BlogUserVO u = currentUser(session);
        Long uid = u != null ? u.getUserId() : null;
        if (!blogService.canReadBlog(blogId, uid)) {
            return "redirect:/auth/login.do?error=forbidden";
        }
        model.addAttribute("blog", blog);
        model.addAttribute("members", blogService.getMembers(blogId));
        model.addAttribute("isOwner", u != null && blogService.isBlogOwner(blogId, u.getUserId()));
        model.addAttribute("canJoinAsMember",
                u != null && u.isApproved() && !blogService.isBlogOwner(blogId, u.getUserId())
                        && blog.getIsPublic());
        return "blog/blogView";
    }

    @GetMapping("/{blogId}/boards.do")
    public String boardList(@PathVariable("blogId") Long blogId, Model model, HttpSession session) {
        BlogVO blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return "redirect:/blog/explore.do";
        }
        BlogUserVO u = currentUser(session);
        Long uid = u != null ? u.getUserId() : null;
        if (!blogService.canReadBlog(blogId, uid)) {
            return "redirect:/auth/login.do?error=forbidden";
        }
        model.addAttribute("blog", blog);
        model.addAttribute("boards", boardService.listBoards(blogId));
        model.addAttribute("isOwner", u != null && blogService.isBlogOwner(blogId, u.getUserId()));
        return "blog/blogBoards";
    }

    @PostMapping("/{blogId}/board/add.do")
    public String addBoard(@PathVariable("blogId") Long blogId,
                           @RequestParam("boardName") String boardName,
                           HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        try {
            boardService.addBoard(blogId, boardName, u);
        } catch (RuntimeException ex) {
            return "redirect:/blog/" + blogId + "/boards.do?error=board";
        }
        return "redirect:/blog/" + blogId + "/boards.do";
    }

    @PostMapping("/{blogId}/board/delete.do")
    public String deleteBoard(@PathVariable("blogId") Long blogId,
                              @RequestParam("boardId") Long boardId,
                              HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        try {
            boardService.removeBoard(blogId, boardId, u);
        } catch (RuntimeException ex) {
            return "redirect:/blog/" + blogId + "/boards.do?error=board_del";
        }
        return "redirect:/blog/" + blogId + "/boards.do";
    }

    @PostMapping("/{blogId}/join.do")
    public String joinBlog(@PathVariable("blogId") Long blogId, HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        try {
            blogService.joinBlog(blogId, u.getUserId());
        } catch (RuntimeException ex) {
            return "redirect:/blog/" + blogId + "/view.do?joinError=1";
        }
        return "redirect:/blog/" + blogId + "/view.do?joinOk=1";
    }

    @GetMapping("/{blogId}/blacklist.do")
    public String blacklistPage(@PathVariable("blogId") Long blogId, Model model, HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!blogService.isBlogOwner(blogId, u.getUserId())) {
            return "redirect:/blog/" + blogId + "/view.do?error=forbidden";
        }
        model.addAttribute("blog", blogService.getBlogById(blogId));
        model.addAttribute("words", moderationService.listBlogBlacklistWords(blogId));
        return "blog/blogBlacklist";
    }

    @PostMapping("/{blogId}/blacklist/add.do")
    public String blacklistAdd(@PathVariable("blogId") Long blogId,
                             @RequestParam("word") String word,
                             HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!blogService.isBlogOwner(blogId, u.getUserId())) {
            return "redirect:/blog/" + blogId + "/view.do?error=forbidden";
        }
        try {
            moderationService.addBlogBlacklistWord(blogId, word);
        } catch (RuntimeException ex) {
            return "redirect:/blog/" + blogId + "/blacklist.do?error=word";
        }
        return "redirect:/blog/" + blogId + "/blacklist.do";
    }

    @PostMapping("/{blogId}/blacklist/delete.do")
    public String blacklistDelete(@PathVariable("blogId") Long blogId,
                                  @RequestParam("blacklistId") Long blacklistId,
                                  HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!blogService.isBlogOwner(blogId, u.getUserId())) {
            return "redirect:/blog/" + blogId + "/view.do?error=forbidden";
        }
        moderationService.deleteBlogBlacklistWord(blogId, blacklistId);
        return "redirect:/blog/" + blogId + "/blacklist.do";
    }

    @GetMapping("/{blogId}/members.do")
    public String memberList(@PathVariable("blogId") Long blogId, Model model, HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!blogService.isBlogOwner(blogId, u.getUserId())) {
            return "redirect:/blog/" + blogId + "/view.do?error=forbidden";
        }
        model.addAttribute("members", blogService.getMembers(blogId));
        model.addAttribute("blogId", blogId);
        return "blog/memberList";
    }

    @PostMapping("/{blogId}/member/status.do")
    public String updateMemberStatus(@PathVariable("blogId") Long blogId,
                                   @RequestParam("memberId") Long memberId,
                                   @RequestParam("status") String status,
                                   HttpSession session) {
        String red = ensureApprovedLogin(session);
        if (red != null) {
            return red;
        }
        BlogUserVO u = currentUser(session);
        if (!blogService.isBlogOwner(blogId, u.getUserId())) {
            return "redirect:/blog/" + blogId + "/view.do?error=forbidden";
        }
        BlogMemberVO member = new BlogMemberVO();
        member.setMemberId(memberId);
        member.setBlogId(blogId);
        member.setStatus(status);
        try {
            blogService.changeMemberStatus(member);
        } catch (IllegalArgumentException ex) {
            return "redirect:/blog/" + blogId + "/members.do?error=status";
        }
        return "redirect:/blog/" + blogId + "/members.do";
    }

    private String ensureApprovedLogin(HttpSession session) {
        BlogUserVO u = currentUser(session);
        if (u == null) {
            return redirectLogin();
        }
        if (!u.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        return null;
    }

    private BlogUserVO currentUser(HttpSession session) {
        Object o = session.getAttribute(LOGIN_USER_SESSION_KEY);
        return o instanceof BlogUserVO ? (BlogUserVO) o : null;
    }

    private String redirectLogin() {
        return "redirect:/auth/login.do?error=login_required";
    }
}