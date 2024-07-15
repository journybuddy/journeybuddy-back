package journeybuddy.spring.service.PostService;

import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;

import java.util.List;

public interface PostCommandService {
    //내가 쓴 게시글 모두조회
    //게시글 상세내용확인
    //게시글 삭제
    //내가 누른 좋아요
    //내가 쓴 댓글확인
    List<Post> checkMyPost(Long userId); //내가 쓴 게시글 모두조회
    Post checkPostDetail(Long userId,Long postId); //게시글 상세내용확인

    Post savePost(User userId, Post post);

    Post deletePost(Long postId);

}
