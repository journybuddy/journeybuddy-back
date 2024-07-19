package journeybuddy.spring.service.ScrapService;

import journeybuddy.spring.converter.CommentConverter;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.converter.ScrapConverter;
import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.domain.mapping.Scrap;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.ScrapRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapRequestDTO;
import journeybuddy.spring.web.dto.ScrapDTO.ScrapResponseDTO;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapCommandServiceImpl implements ScrapCommandService {

    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //대충 저장하기 save로직
    public Scrap saveScrap(String userEmail,Long postId, Scrap scrap) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
        scrap.setUser(user);
        scrap.setPost(post);
        log.info("Saving post: {}", scrap);
        return scrapRepository.save(scrap);
    }

    //내가 스크랩한 게시물 확인하기
    public Page<ScrapResponseDTO> findAll(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));


        Page<Scrap> scrapsByUser = scrapRepository.findAllByUser(user,pageable);
        return scrapsByUser.map(ScrapConverter::toScrapResponseDTO);

    }
}
