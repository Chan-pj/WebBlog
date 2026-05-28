package com.chan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.chan.vo.PostVO;

@Mapper
public interface PostMapper {
    int insertPost(PostVO post);

    int updatePost(PostVO post);

    int deletePost(Long postId);

    PostVO selectPostById(Long postId);

    List<PostVO> selectRootPostsByBoardId(Long boardId);

    List<PostVO> selectRepliesByParentPostId(Long parentPostId);
}
