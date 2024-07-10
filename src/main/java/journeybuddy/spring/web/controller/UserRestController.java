package journeybuddy.spring.web.controller;

import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.UserService.CustomUserDetails;
import journeybuddy.spring.service.UserService.CustomUserDetailsService;
import journeybuddy.spring.service.UserService.UserCommandService;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
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

    @Autowired
    private final UserCommandService userCommandService;
    @Autowired
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private final CustomUserDetailsService customUserDetailsService;

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

            // 현재 인증된 사용자의 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                response.put("username", authentication.getName()); // 사용자 이름
                response.put("authorities", authentication.getAuthorities()); // 권한 목록
            }

            log.info("회원가입 완료");
            log.info("현재 사용자: {}", authentication != null ? authentication.getName() : "인증되지 않음");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원가입 중 문제 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 문제가 발생했습니다.");
        }
    }

    @PostMapping("/update/{userEmail}")
    public ResponseEntity<?> updateForm(@PathVariable("userEmail") String email,
                                        @RequestBody @Valid UserRequestDTO.UpdateDTO request,
                                        BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            // 사용자 업데이트 로직 수행
            userCommandService.updateUser(request, email);
            log.info("사용자 업데이트 완료");

            return ResponseEntity.ok().build();
        } catch (TempHandler e) {
            log.error("사용자 업데이트 실패: {}", e.getErrorReasonHttpStatus());
            return null;
        }
    }

    @GetMapping("/update/{userEmail}")
    public ResponseEntity<?> showUpdateForm(@PathVariable("userEmail") String userEmail) {
        Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
        String username = userDetails.getName();
        String password = (String) userDetails.getCredentials();

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
    public ResponseEntity<?> loginForm(@RequestBody @Valid UserRequestDTO.UpdateDTO request) {
        try {
            // 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 인증 성공한 경우
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // CustomUserDetails에서 사용자 정보 추출
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(authentication);

            // 응답에 사용자 정보와 토큰 포함
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getEmail());
            response.put("token", accessToken);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        httpSession.invalidate();
        log.info("로그아웃 됨");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId, HttpSession httpSession) {
        userCommandService.deletedById(userId);
        httpSession.invalidate();
        log.info("탈퇴 처리 완료");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    public ResponseEntity<String> showHome() {
        return ResponseEntity.ok("Home page");
    }

    @GetMapping("/all")
    public String getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            // 인증되지 않은 경우 처리
            throw new UsernameNotFoundException("유저 정보를 찾을 수 없습니다.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        // 사용자 정보 반환 또는 처리
        return "현재 사용자: " + username;
    }
}

/*

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
public class UserRestController {

    private final UserCommandService userCommandService;

    private final UserRepository userRepository;

    @PostMapping("/add")
    @ApiOperation(value = "Add a new user")
    public ApiResponse<UserResponseDTO.UpdateResultDTO> addUser(@RequestBody @Valid UserRequestDTO.UpdateDTO request) {

        if(userRepository.existsByEmail(request.getEmail())){
            return ApiResponse.onFailure("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.", null);
        }else{
            User addUser = userCommandService.addUser(request);
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(addUser));
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

    @GetMapping
    public ApiResponse<UserResponseDTO.UpdateResultDTO> getUserById(@RequestParam("id") Long userId) {
        User user = userCommandService.getUserById(userId);
        if (user == null) {
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(user));
    }

    @PutMapping("/update/{userId}")
    @ApiOperation(value = "update a new user")
    public ApiResponse<UserResponseDTO.UpdateResultDTO> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid UserRequestDTO.UpdateDTO request) {
        request.setId(userId);

        if(userRepository.existsByEmail(request.getEmail())){
            return ApiResponse.onFailure("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.", null);
        }
        else{
            User updatedUser = userCommandService.updateUser(request);
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(updatedUser));
        }
    }

    @DeleteMapping
    @ApiOperation(value = "회원탈퇴기능")
    public ApiResponse<Object> deleteUser(@RequestParam("id") Long userId) {
        User deletedUser = userCommandService.deletedById(userId);
        return ApiResponse.deleteSuccess();
    }

 */