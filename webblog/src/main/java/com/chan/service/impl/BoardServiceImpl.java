package com.chan.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chan.mapper.BoardMapper;
import com.chan.service.BlogService;
import com.chan.service.BoardService;
import com.chan.vo.BoardVO;
import com.chan.vo.BlogUserVO;
import com.chan.vo.BlogVO;

@Service("boardService")
public class BoardServiceImpl implements BoardService {

    @Resource
    private BoardMapper boardMapper;

    @Resource
    private BlogService blogService;

    @Override
    public List<BoardVO> listBoards(Long blogId) {
        return boardMapper.selectBoardsByBlogId(blogId);
    }

    @Override
    public BoardVO getBoard(Long boardId) {
        return boardMapper.selectBoardById(boardId);
    }

    @Override
    public void addBoard(Long blogId, String boardName, BlogUserVO loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        if (!blogService.isBlogOwner(blogId, loginUser.getUserId())) {
            throw new IllegalStateException("블로그 소유자만 게시판을 만들 수 있습니다.");
        }
        if (!loginUser.isCanCreateBoard()) {
            throw new IllegalStateException("게시판 생성 권한이 없습니다. 슈퍼 관리자에게 문의하세요.");
        }
        if (boardName == null || boardName.trim().isEmpty()) {
            throw new IllegalArgumentException("게시판 이름을 입력해 주세요.");
        }
        BlogVO blog = blogService.getBlogById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("블로그를 찾을 수 없습니다.");
        }
        int cnt = boardMapper.countBoardsByBlogId(blogId);
        if (cnt >= blog.getBoardLimit()) {
            throw new IllegalStateException("게시판 개수 상한에 도달했습니다.");
        }
        BoardVO b = new BoardVO();
        b.setBlogId(blogId);
        b.setBoardName(boardName.trim());
        b.setSortOrder(cnt);
        boardMapper.insertBoard(b);
    }

    @Override
    public void removeBoard(Long blogId, Long boardId, BlogUserVO loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        if (!blogService.isBlogOwner(blogId, loginUser.getUserId())) {
            throw new IllegalStateException("블로그 소유자만 게시판을 삭제할 수 있습니다.");
        }
        BoardVO board = boardMapper.selectBoardById(boardId);
        if (board == null || !blogId.equals(board.getBlogId())) {
            throw new IllegalArgumentException("게시판을 찾을 수 없습니다.");
        }
        if (boardMapper.countBoardsByBlogId(blogId) <= 1) {
            throw new IllegalStateException("마지막 게시판은 삭제할 수 없습니다.");
        }
        if (boardMapper.countPostsByBoardId(boardId) > 0) {
            throw new IllegalStateException("글이 있는 게시판은 삭제할 수 없습니다.");
        }
        boardMapper.deleteBoardById(boardId, blogId);
    }
}
