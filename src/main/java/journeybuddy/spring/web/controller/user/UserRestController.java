package journeybuddy.spring.web.controller.user;

import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.converter.user.UserUpdateConverter;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.service.user.UserCommandService;
import journeybuddy.spring.web.dto.user.UserRequestDTO;
import journeybuddy.spring.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
public class UserRestController {

    private final UserCommandService userCommandService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;


    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<User> updateUser(
            @RequestPart(required = false) String bio,
    //        @RequestParam(required = false) String nickname,
            @RequestParam(required = false) MultipartFile profileImage,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        UserRequestDTO.UpdateDTO request = new UserRequestDTO.UpdateDTO();
        request.setBio(bio);
    //    request.setNickname(nickname);

        // 서비스 호출
        User user = userCommandService.updateUser(request, profileImage, userEmail);

        log.info("회원 업데이트 완료");
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId,
                                        @RequestBody @Valid UserRequestDTO.LoginDTO request) {

        String email = String.valueOf(userCommandService.getUserEmailById(userId));
        User user = userCommandService.getUserByEmail(email);
        String encodedPassword = user.getPassword();

        if (!request.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("common403", "접근권한없는사용자", null));
        }

        try {
            userCommandService.deletedById(userId);
            log.info("탈퇴 처리 완료");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("뭔가 잘못됨", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("common500", "서버문제발생", null));
        }
    }


    //convert로직 service로 이동시키기
    @GetMapping("/all")
    public ApiResponse<List<UserResponseDTO.UpdateResultDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO.UpdateResultDTO> result = users.stream()
                .map(UserUpdateConverter::toUpdateResultDTO)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(result);
    }
}
