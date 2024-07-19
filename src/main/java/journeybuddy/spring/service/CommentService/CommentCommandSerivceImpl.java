package journeybuddy.spring.service.CommentService;

import journeybuddy.spring.converter.CommentConverter;
import journeybuddy.spring.converter.PostConverter;
import journeybuddy.spring.domain.Comment;
import journeybuddy.spring.domain.Post;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.CommentRepository;
import journeybuddy.spring.repository.PostRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.web.dto.CommentDTO.CommentResponseDTO;
import journeybuddy.spring.web.dto.PostDTO.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommandSerivceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

/*
    @Override
    public Page<CommentResponseDTO> checkMyComment(String userEmail, Pageable pageable) {
        try {
            if (userEmail != null) {
                Page<Comment> myComment = commentRepository.findAllByUser(userEmail, pageable);
                commentRepository.findAll();
                return myComment.map(CommentConverter::toCommentResponseDTO);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }
*/



    public Page<CommentResponseDTO> checkMyComment(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(()->{
            return new UsernameNotFoundException("User not found with email: " + userEmail);
        });
        Page<Comment> commentsByUser = commentRepository.findAllByUser(user, pageable); // 사용자 객체를 인자로 전달
        return commentsByUser.map(CommentConverter::toCommentResponseDTO);
    }

    private User checkUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }
    @Override
    public Comment commentSave(String userEmail, Long postId, Comment comment) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
        Post post  = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당계정이 존재하지 않습니다"));
            comment.setUser(user);
            comment.setPost(post);
            Comment savedComment = commentRepository.save(comment);
            return savedComment;
        }



}
