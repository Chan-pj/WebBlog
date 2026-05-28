package com.chan.service;

import java.util.List;

import com.chan.vo.BoardVO;
import com.chan.vo.BlogUserVO;

public interface BoardService {

    List<BoardVO> listBoards(Long blogId);

    BoardVO getBoard(Long boardId);

    void addBoard(Long blogId, String boardName, BlogUserVO loginUser);

    void removeBoard(Long blogId, Long boardId, BlogUserVO loginUser);
}
