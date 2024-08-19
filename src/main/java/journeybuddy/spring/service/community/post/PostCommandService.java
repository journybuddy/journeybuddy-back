package journeybuddy.spring.service.community.post;

import journeybuddy.spring.domain.community.Post;
import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCommandService {
    List<Post> checkMyPost(String email); //내가 쓴 게시글 모두조회
    Post checkPostDetail(Long postId, String authentication); //게시글 상세내용확인
//    Post savePost(String userEmail, Post post);
    Post deletePost(Long postId,String authentication);
    List<PostListResponse> getPosts(Pageable pageable);
    Page<PostListResponse> getMyPeed(String userName, Pageable pageable);

}
