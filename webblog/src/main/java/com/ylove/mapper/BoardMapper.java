package com.ylove.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ylove.vo.BoardVO;

@Mapper
public interface BoardMapper {
    int insertBoard(BoardVO board);

    BoardVO selectBoardById(Long boardId);

    List<BoardVO> selectBoardsByBlogId(Long blogId);

    int countBoardsByBlogId(Long blogId);

    int deleteBoardById(@Param("boardId") Long boardId, @Param("blogId") Long blogId);

    int countPostsByBoardId(Long boardId);
}
