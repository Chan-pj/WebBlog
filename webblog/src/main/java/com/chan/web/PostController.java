package com.chan.web;

import java.util.Collections;
import java.util.List;

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
import com.chan.service.PostService;
import com.chan.util.PagingUtil;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;
import com.chan.vo.BoardVO;
import com.chan.vo.PostVO;

@Controller
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private BlogService blogService;

    @Resource
    private BoardService boardService;

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    @GetMapping("/{blogId}/list.do")
    public String listLegacyRedirect(@PathVariable("blogId") Long blogId) {
        List<BoardVO> boards = boardService.listBoards(blogId);
        if (boards == null || boards.isEmpty()) {
            return "redirect:/blog/" + blogId + "/boards.do?error=no_board";
        }
        BoardVO first = boards.get(0);
        return "redirect:/post/" + blogId + "/board/" + first.getBoardId() + "/list.do";
    }

    @GetMapping("/{blogId}/board/{boardId}/list.do")
    public String list(@PathVariable("blogId") Long blogId,
                       @PathVariable("boardId") Long boardId,
                       @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                       Model model,
                       HttpSession session) {
        BoardVO board = boardService.getBoard(boardId);
        if (board == null || !blogId.equals(board.getBlogId())) {
            return "redirect:/";
        }
        BlogUserVO loginUser = getLoginUser(session);
        Long uid = loginUser != null ? loginUser.getUserId() : null;
        if (!blogService.canReadBlog(blogId, uid)) {
            return "redirect:/auth/login.do?error=forbidden";
        }
        PaginationInfo paging = PagingUtil.paginate(pageIndex, postService.countRootPostsByBoardId(boardId));
        model.addAttribute("posts", postService.getRootPostsByBoardId(
                boardId, paging.getFirstRecordIndex(), paging.getRecordCountPerPage()));
        model.addAttribute("paginationInfo", paging);
        model.addAttribute("blogId", blogId);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
        model.addAttribute("canWrite", canWrite(blogId, session));
        return "post/postList";
    }

    @GetMapping("/{blogId}/board/{boardId}/write.do")
    public String writeForm(@PathVariable("blogId") Long blogId,
                            @PathVariable("boardId") Long boardId,
                            Model model,
                            HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login.do?error=login_required";
        }
        if (!loginUser.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        if (!blogService.canWritePost(blogId, loginUser.getUserId())) {
            return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do?error=write_not_allowed";
        }
        BoardVO board = boardService.getBoard(boardId);
        if (board == null || !blogId.equals(board.getBlogId())) {
            return "redirect:/";
        }
        model.addAttribute("blogId", blogId);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
        return "post/postForm";
    }

    @PostMapping("/{blogId}/board/{boardId}/write.do")
    public String write(@PathVariable("blogId") Long blogId,
                        @PathVariable("boardId") Long boardId,
                        @RequestParam("title") String title,
                        @RequestParam("content") String content,
                        HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login.do?error=login_required";
        }
        if (!loginUser.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        if (!blogService.canWritePost(blogId, loginUser.getUserId())) {
            return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do?error=write_not_allowed";
        }
        BoardVO board = boardService.getBoard(boardId);
        if (board == null || !blogId.equals(board.getBlogId())) {
            return "redirect:/";
        }
        PostVO post = new PostVO();
        post.setBlogId(blogId);
        post.setBoardId(boardId);
        post.setWriterUserId(loginUser.getUserId());
        post.setParentPostId(null);
        post.setTitle(title);
        post.setContent(content);
        try {
            postService.createPost(post);
        } catch (IllegalArgumentException ex) {
            return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do?error=validation";
        }
        return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do";
    }

    @PostMapping("/{blogId}/board/{boardId}/reply.do")
    public String reply(@PathVariable("blogId") Long blogId,
                        @PathVariable("boardId") Long boardId,
                        @RequestParam("parentPostId") Long parentPostId,
                        @RequestParam("title") String title,
                        @RequestParam("content") String content,
                        HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login.do?error=login_required";
        }
        if (!loginUser.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        BoardVO board = boardService.getBoard(boardId);
        if (board == null || !blogId.equals(board.getBlogId())) {
            return "redirect:/";
        }
        if (!blogService.canReadBlog(blogId, loginUser.getUserId())) {
            return "redirect:/auth/login.do?error=forbidden";
        }
        // 답글은 블로그 소유자만 작성할 수 있다. (PostServiceImpl 에서도 재검증)
        if (!blogService.isBlogOwner(blogId, loginUser.getUserId())) {
            return "redirect:/post/view/" + parentPostId + ".do?error=forbidden";
        }
        PostVO parent = postService.getPostById(parentPostId);
        if (parent == null || !blogId.equals(parent.getBlogId()) || !boardId.equals(parent.getBoardId())) {
            return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do";
        }
        PostVO post = new PostVO();
        post.setBlogId(blogId);
        post.setBoardId(boardId);
        post.setWriterUserId(loginUser.getUserId());
        post.setParentPostId(parentPostId);
        post.setTitle(title != null && !title.trim().isEmpty() ? title.trim() : "답글");
        post.setContent(content);
        try {
            postService.createPost(post);
        } catch (IllegalArgumentException ex) {
            return "redirect:/post/view/" + parentPostId + ".do?error=validation";
        }
        return "redirect:/post/view/" + parentPostId + ".do";
    }

    @GetMapping("/view/{postId}.do")
    public String view(@PathVariable("postId") Long postId, Model model, HttpSession session) {
        PostVO post = postService.getPostById(postId);
        if (post == null) {
            return "redirect:/";
        }
        BlogUserVO loginUser = getLoginUser(session);
        Long uid = loginUser != null ? loginUser.getUserId() : null;
        if (!blogService.canReadBlog(post.getBlogId(), uid)) {
            return "redirect:/auth/login.do?error=forbidden";
        }
        model.addAttribute("post", post);
        if (post.getParentPostId() == null) {
            model.addAttribute("replies", postService.getRepliesByParentPostId(postId));
        } else {
            model.addAttribute("replies", Collections.emptyList());
        }
        boolean owner = loginUser != null && loginUser.isApproved()
                && blogService.isBlogOwner(post.getBlogId(), loginUser.getUserId());
        model.addAttribute("canReply", owner && post.getParentPostId() == null);
        model.addAttribute("canManagePost", canManagePost(loginUser, post));
        return "post/postView";
    }

    @PostMapping("/update.do")
    public String update(@RequestParam("postId") Long postId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login.do?error=login_required";
        }
        if (!loginUser.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        PostVO original = postService.getPostById(postId);
        if (original == null) {
            return "redirect:/";
        }
        if (!canManagePost(loginUser, original)) {
            return "redirect:/post/view/" + original.getPostId() + ".do?error=forbidden";
        }
        PostVO post = new PostVO();
        post.setPostId(postId);
        post.setTitle(title);
        post.setContent(content);
        post.setBlogId(original.getBlogId());
        post.setBoardId(original.getBoardId());
        post.setWriterUserId(original.getWriterUserId());
        post.setParentPostId(original.getParentPostId());
        try {
            postService.updatePost(post);
        } catch (IllegalArgumentException ex) {
            return "redirect:/post/view/" + post.getPostId() + ".do?error=validation";
        }
        return "redirect:/post/view/" + post.getPostId() + ".do";
    }

    @PostMapping("/delete.do")
    public String delete(@RequestParam("postId") Long postId,
                         @RequestParam("blogId") Long blogId,
                         @RequestParam(value = "boardId", required = false) Long boardId,
                         HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null) {
            return "redirect:/auth/login.do?error=login_required";
        }
        if (!loginUser.isApproved()) {
            return "redirect:/auth/pendingApproval.do";
        }
        PostVO original = postService.getPostById(postId);
        if (original == null) {
            return "redirect:/post/" + blogId + "/list.do";
        }
        if (!canManagePost(loginUser, original)) {
            String b = boardId != null ? boardId.toString() : "";
            if (!b.isEmpty()) {
                return "redirect:/post/" + blogId + "/board/" + b + "/list.do?error=forbidden";
            }
            return "redirect:/post/" + blogId + "/list.do?error=forbidden";
        }
        postService.deletePost(postId);
        if (boardId != null) {
            return "redirect:/post/" + blogId + "/board/" + boardId + "/list.do";
        }
        return "redirect:/post/" + blogId + "/list.do";
    }

    private BlogUserVO getLoginUser(HttpSession session) {
        Object user = session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (!(user instanceof BlogUserVO)) {
            return null;
        }
        return (BlogUserVO) user;
    }

    private boolean canWrite(Long blogId, HttpSession session) {
        BlogUserVO loginUser = getLoginUser(session);
        if (loginUser == null || !loginUser.isApproved()) {
            return false;
        }
        return blogService.canWritePost(blogId, loginUser.getUserId());
    }

    private boolean canManagePost(BlogUserVO loginUser, PostVO post) {
        if (loginUser == null || post == null || !loginUser.isApproved()) {
            return false;
        }
        if (loginUser.getUserId().equals(post.getWriterUserId())) {
            return true;
        }
        BlogVO blog = blogService.getBlogById(post.getBlogId());
        return blog != null && loginUser.getUserId().equals(blog.getOwnerUserId());
    }
}
