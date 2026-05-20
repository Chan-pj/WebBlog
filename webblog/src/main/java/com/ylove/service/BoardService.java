package com.ylove.service;

import java.util.List;

import com.ylove.vo.BoardVO;
import com.ylove.vo.BlogUserVO;

public interface BoardService {

    List<BoardVO> listBoards(Long blogId);

    BoardVO getBoard(Long boardId);

    void addBoard(Long blogId, String boardName, BlogUserVO loginUser);

    void removeBoard(Long blogId, Long boardId, BlogUserVO loginUser);
}
