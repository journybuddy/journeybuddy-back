package journeybuddy.spring.service.PostService;

import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;

import java.util.List;

public interface PostCommandService {
    //내가 쓴 게시글 모두조회
    //게시글 상세내용확인
    //게시글 삭제
    //내가 누른 좋아요
    //내가 쓴 댓글확인
    List<Post> checkMyPost(String email); //내가 쓴 게시글 모두조회
    Post checkPostDetail(Long postId); //게시글 상세내용확인

    Post savePost(String userEmail, Post post);

    Post deletePost(Long postId);

}
