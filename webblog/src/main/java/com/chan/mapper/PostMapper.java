package com.chan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.chan.vo.PostVO;

@Mapper
public interface PostMapper {
    int insertPost(PostVO post);

    int updatePost(PostVO post);

    int deletePost(Long postId);

    int deletePostsByBlogId(Long blogId);

    int deletePostsByWriterUserId(Long writerUserId);

    PostVO selectPostById(Long postId);

    List<PostVO> selectRootPostsByBoardId(@Param("boardId") Long boardId,
                                          @Param("offset") int offset, @Param("limit") int limit);

    int countRootPostsByBoardId(Long boardId);

    List<PostVO> selectRepliesByParentPostId(Long parentPostId);
}
