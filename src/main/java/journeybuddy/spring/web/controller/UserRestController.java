package journeybuddy.spring.web.controller;

import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.RefreshToken;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RefreshTokenRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.PostService.PostCommandServiceImpl;
import journeybuddy.spring.service.UserService.UserCommandService;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final PostCommandServiceImpl postCommandServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> registrationForm(@RequestBody @Valid UserRequestDTO.RegisterDTO request,
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

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserRequestDTO.UpdateDTO request,@AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        userCommandService.updateUser(request,userEmail);
        log.info("회원업데이트 완료");
        return ResponseEntity.ok().build();
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId,
                                        @RequestBody @Valid UserRequestDTO.LoginDTO request) {

        String email = String.valueOf(userCommandService.getUserEmailById(userId));
        User user = userCommandService.getUserByEmail(email);
        String encodedPassword = user.getPassword();

        if (!request.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure("common403", "접근권한없는사용자", null));
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), encodedPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("common401", "비밀번호가 일치하지 않음", null));
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


    @GetMapping("/all")
    public ApiResponse<List<UserResponseDTO.UpdateResultDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO.UpdateResultDTO> result = users.stream()
                .map(UserUpdateConverter::toUpdateResultDTO)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/test") //인증확인용 메소드
    @PreAuthorize("isAuthenticated()")
    public String test() {
        return "success";
    }


}
