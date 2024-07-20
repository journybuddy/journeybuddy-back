package journeybuddy.spring.service.PostService;

import journeybuddy.spring.domain.Post;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCommandService {
    List<Post> checkMyPost(String email); //내가 쓴 게시글 모두조회
    Post checkPostDetail(Long postId, String authentication); //게시글 상세내용확인
    Post savePost(String userEmail, Post post);
    Post deletePost(Long postId,String authentication);
    List<PostResponseDTO> getPosts(Pageable pageable);
    Page<PostResponseDTO> getMyPeed(String userName, Pageable pageable);

}
