package com.ylove.service;

import java.util.List;

import com.ylove.vo.PostVO;

public interface PostService {
    void createPost(PostVO post);

    void updatePost(PostVO post);

    void deletePost(Long postId);

    PostVO getPostById(Long postId);

    List<PostVO> getRootPostsByBoardId(Long boardId);

    List<PostVO> getRepliesByParentPostId(Long parentPostId);
}
