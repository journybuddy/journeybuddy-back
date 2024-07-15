package journeybuddy.spring.web.controller;

import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.RefreshToken;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RefreshTokenRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.UserService.CustomUserDetails;
import journeybuddy.spring.service.UserService.UserCommandService;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
public class UserRestController {

    private final UserCommandService userCommandService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @GetMapping("/register")
    public ResponseEntity<UserRequestDTO.UpdateDTO> getRegistrationForm() {
        return ResponseEntity.ok(new UserRequestDTO.UpdateDTO());
    }
    @PostMapping("/register")
    public ResponseEntity<?> registrationForm(@RequestBody @Valid UserRequestDTO.UpdateDTO request,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            User newUser = userCommandService.addUser(request);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", newUser.getId());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                response.put("username", authentication.getName()); // 사용자 이름
                response.put("authorities", authentication.getAuthorities()); // 권한 목록
            }
            log.info("회원가입 완료");
            log.info("현재 사용자: {}", authentication != null ? authentication.getName() : "인증되지 않음");

            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } catch (Exception e) {
            log.error("이미 존재하는 사용자임", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("Duplicated Email","회원가입 중 문제발생", null));
        }
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateForm(@PathVariable("userId") Long id,
                                        @RequestBody @Valid UserRequestDTO.UpdateDTO request,
                                        BindingResult bindingResult) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            String userEmail = String.valueOf(userCommandService.getUserEmailById(id));

            if (!authentication.getName().equals(userEmail)) {
                log.error("접근 권한 없는 사용자");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.onFailure("common403","접근권한없는사용자",null));
            }

            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }

            User updatedUser = userCommandService.updateUser(request, userEmail);
            log.info("사용자 업데이트 완료");
            return ResponseEntity.ok(ApiResponse.onSuccess(updatedUser));
        } catch (TempHandler e) {
            log.error("사용자 업데이트 실패: {}", e.getErrorReasonHttpStatus());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 업데이트 실패");
        }
    }

    @GetMapping("/update/{userEmail}")
    public ResponseEntity<?> showUpdateForm(@PathVariable("userEmail") String userEmail) {
        Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
        String username = userDetails.getName();

        String email = userDetails.getName();
        if (!email.equals(userEmail)) {
            log.error("접근 권한 없는 사용자");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한 없음");
        }
        try {
            User user = userCommandService.getUserByEmail(userEmail); // userEmail을 기준으로 사용자 정보를 조회
            UserResponseDTO.UpdateResultDTO userDTO = UserUpdateConverter.toUpdateResultDTO(user);
            log.info("접근 권한 있는 사용자");
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            log.error("사용자 정보 조회 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> getLoginForm() {
        return ResponseEntity.ok("Login page");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginForm(@RequestBody @Valid UserRequestDTO.LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken();


            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .key(user.getEmail())
                    .value(refreshToken)
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("userEmail", user.getEmail());
            response.put("token", accessToken);
            response.put("refreshToken",refreshToken);

            log.info("로그인완료");
            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } catch (AuthenticationException e) {
            log.error("인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId,
                                        @RequestBody @Valid UserRequestDTO.LoginDTO request) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String userEmail = String.valueOf(userCommandService.getUserEmailById(userId));

        User user = userCommandService.getUserByEmail(userEmail);
        String encodedPassword = user.getPassword();


        try {
            if (authentication.getName().equals(userEmail)) {
                if (bCryptPasswordEncoder.matches(request.getPassword(), encodedPassword)) {
                    userCommandService.deletedById(userId);
                    log.info("탈퇴 처리 완료");
                    return ResponseEntity.ok().build();
                } else {
                    log.error("비밀번호가 일치하지 않음");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.onFailure("common401","비밀번호가 일치하지 않음",null));
                }
            } else {
                log.error("인증 실패");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.onFailure("common401","이메일이 일치하지 않습니다", null));
            }
        } catch (RuntimeException e) {
            log.error("뭔가 잘못됨", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("common500","서버문제발생",null));
        }
    }

    @GetMapping("/home")
    public ResponseEntity<String> showHome() {
        return ResponseEntity.ok("Home page");
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponseDTO.UpdateResultDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO.UpdateResultDTO> result = users.stream()
                .map(UserUpdateConverter::toUpdateResultDTO)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/user/get")
    public ResponseEntity<User> getUser(@RequestParam String userEmail) throws Exception {
        return new ResponseEntity<>( userCommandService.getUserByEmail(userEmail), HttpStatus.OK);
    }
}
