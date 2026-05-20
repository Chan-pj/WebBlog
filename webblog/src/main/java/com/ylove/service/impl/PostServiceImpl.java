package com.ylove.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ylove.mapper.PostMapper;
import com.ylove.service.BlogService;
import com.ylove.service.ModerationService;
import com.ylove.service.PostService;
import com.ylove.vo.PostVO;

@Service("postService")
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private ModerationService moderationService;

    @Resource
    private BlogService blogService;

    @Override
    public void createPost(PostVO post) {
        validateNewPost(post);
        postMapper.insertPost(post);
    }

    @Override
    public void updatePost(PostVO post) {
        validateContent(post);
        postMapper.updatePost(post);
    }

    @Override
    public void deletePost(Long postId) {
        postMapper.deletePost(postId);
    }

    @Override
    public PostVO getPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    public List<PostVO> getRootPostsByBoardId(Long boardId) {
        return postMapper.selectRootPostsByBoardId(boardId);
    }

    @Override
    public List<PostVO> getRepliesByParentPostId(Long parentPostId) {
        return postMapper.selectRepliesByParentPostId(parentPostId);
    }

    private void validateNewPost(PostVO post) {
        if (post.getParentPostId() == null) {
            validateContent(post);
            return;
        }

        PostVO parent = postMapper.selectPostById(post.getParentPostId());
        if (parent == null) {
            throw new IllegalArgumentException("원글을 찾을 수 없습니다.");
        }
        if (!parent.getBlogId().equals(post.getBlogId())) {
            throw new IllegalArgumentException("블로그가 일치하지 않습니다.");
        }
        if (parent.getParentPostId() != null) {
            throw new IllegalArgumentException("답글에는 다시 답글을 달 수 없습니다.");
        }
        if (!parent.getBoardId().equals(post.getBoardId())) {
            throw new IllegalArgumentException("게시판이 일치하지 않습니다.");
        }
        if (!blogService.isBlogOwner(post.getBlogId(), post.getWriterUserId())) {
            throw new IllegalArgumentException("답글은 블로그 관리자(소유자)만 작성할 수 있습니다.");
        }
        validateContent(post);
    }

    private void validateContent(PostVO post) {
        String merged = post.getTitle() + " " + post.getContent();

        if (moderationService.containsBlockedWord(post.getBlogId(), merged)) {
            throw new IllegalArgumentException("금지 단어가 포함된 게시글은 등록할 수 없습니다.");
        }

        if (moderationService.containsPersonalInfo(merged)) {
            throw new IllegalArgumentException("개인정보가 포함된 게시글은 등록할 수 없습니다.");
        }
    }
}
